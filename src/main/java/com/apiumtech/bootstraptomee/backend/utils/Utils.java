package com.apiumtech.bootstraptomee.backend.utils;

import com.google.common.collect.Lists;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.i18n.shared.BidiUtils;
import com.steadystate.css.parser.CSSOMParser;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.mozilla.universalchardet.UniversalDetector;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleDeclaration;

import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility methods related to feed handling
 * 
 */
@Slf4j
public class Utils {

	private static final String ESCAPED_QUESTION_MARK = Pattern.quote("?");

	private static final List<String> ALLOWED_IFRAME_CSS_RULES = Arrays.asList("height", "width", "border");
	private static final List<String> ALLOWED_IMG_CSS_RULES = Arrays.asList("display", "width", "height");
	private static final char[] FORBIDDEN_CSS_RULE_CHARACTERS = new char[] { '(', ')' };

	public static String truncate(String string, int length) {
		if (string != null) {
			string = string.substring(0, Math.min(length, string.length()));
		}
		return string;
	}

	/**
	 * Detect feed encoding by using the declared encoding in the xml processing instruction and by detecting the characters used in the
	 * feed
	 * 
	 */
	public static String guessEncoding(byte[] bytes) {
		String extracted = extractDeclaredEncoding(bytes);
		if (StringUtils.startsWithIgnoreCase(extracted, "iso-8859-")) {
			if (StringUtils.endsWith(extracted, "1") == false) {
				return extracted;
			}
		} else if (StringUtils.startsWithIgnoreCase(extracted, "windows-")) {
			return extracted;
		}
		return detectEncoding(bytes);
	}

	/**
	 * Detect encoding by analyzing characters in the array
	 */
	public static String detectEncoding(byte[] bytes) {
		String DEFAULT_ENCODING = "UTF-8";
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(bytes, 0, bytes.length);
		detector.dataEnd();
		String encoding = detector.getDetectedCharset();
		detector.reset();
		if (encoding == null) {
			encoding = DEFAULT_ENCODING;
		} else if (encoding.equalsIgnoreCase("ISO-8859-1")) {
			encoding = "windows-1252";
		}
		return encoding;
	}

	/**
	 * Normalize the url. The resulting url is not meant to be fetched but rather used as a mean to identify a feed and avoid duplicates
	 */
	public static String normalizeURL(String url) {
		if (url == null) {
			return null;
		}
		String normalized = URLCanonicalizer.getCanonicalURL(url);
		if (normalized == null) {
			normalized = url;
		}

		// convert to lower case, the url probably won't work in some cases
		// after that but we don't care we just want to compare urls to avoid
		// duplicates
		normalized = normalized.toLowerCase();

		// store all urls as http
		if (normalized.startsWith("https")) {
			normalized = "http" + normalized.substring(5);
		}

		// remove the www. part
		normalized = normalized.replace("//www.", "//");

		// feedproxy redirects to feedburner
		normalized = normalized.replace("feedproxy.google.com", "feeds.feedburner.com");

		// feedburner feeds have a special treatment
		if (normalized.split(ESCAPED_QUESTION_MARK)[0].contains("feedburner.com")) {
			normalized = normalized.replace("feeds2.feedburner.com", "feeds.feedburner.com");
			normalized = normalized.split(ESCAPED_QUESTION_MARK)[0];
			normalized = StringUtils.removeEnd(normalized, "/");
		}

		return normalized;
	}

	/**
	 * Extract the declared encoding from the xml
	 */
	public static String extractDeclaredEncoding(byte[] bytes) {
		int index = ArrayUtils.indexOf(bytes, (byte) '>');
		if (index == -1) {
			return null;
		}

		String pi = new String(ArrayUtils.subarray(bytes, 0, index + 1));
		index = StringUtils.indexOf(pi, "encoding=\"");
		if (index == -1) {
			return null;
		}
		String encoding = pi.substring(index + 10, pi.length());
		encoding = encoding.substring(0, encoding.indexOf('"'));
		return encoding;
	}

	public static String handleContent(String content, String baseUri, boolean keepTextOnly) {
		if (StringUtils.isNotBlank(content)) {
			baseUri = StringUtils.trimToEmpty(baseUri);
			Whitelist whitelist = new Whitelist();
			whitelist.addTags("a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "dl", "dt", "em",
					"h1", "h2", "h3", "h4", "h5", "h6", "i", "iframe", "img", "li", "ol", "p", "pre", "q", "small", "strike", "strong",
					"sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul");

			whitelist.addAttributes("div", "dir");
			whitelist.addAttributes("pre", "dir");
			whitelist.addAttributes("code", "dir");
			whitelist.addAttributes("table", "dir");
			whitelist.addAttributes("p", "dir");
			whitelist.addAttributes("a", "href", "title");
			whitelist.addAttributes("blockquote", "cite");
			whitelist.addAttributes("col", "span", "width");
			whitelist.addAttributes("colgroup", "span", "width");
			whitelist.addAttributes("iframe", "src", "height", "width", "allowfullscreen", "frameborder", "style");
			whitelist.addAttributes("img", "align", "alt", "height", "src", "title", "width", "style");
			whitelist.addAttributes("ol", "start", "type");
			whitelist.addAttributes("q", "cite");
			whitelist.addAttributes("table", "border", "bordercolor", "summary", "width");
			whitelist.addAttributes("td", "border", "bordercolor", "abbr", "axis", "colspan", "rowspan", "width");
			whitelist.addAttributes("th", "border", "bordercolor", "abbr", "axis", "colspan", "rowspan", "scope", "width");
			whitelist.addAttributes("ul", "type");

			whitelist.addProtocols("a", "href", "ftp", "http", "https", "mailto");
			whitelist.addProtocols("blockquote", "cite", "http", "https");
			whitelist.addProtocols("img", "src", "http", "https");
			whitelist.addProtocols("q", "cite", "http", "https");

			whitelist.addEnforcedAttribute("a", "target", "_blank");

			Document dirty = Jsoup.parseBodyFragment(content, baseUri);
			Cleaner cleaner = new Cleaner(whitelist);
			Document clean = cleaner.clean(dirty);

			for (Element e : clean.select("iframe[style]")) {
				String style = e.attr("style");
				String escaped = escapeIFrameCss(style);
				e.attr("style", escaped);
			}

			for (Element e : clean.select("img[style]")) {
				String style = e.attr("style");
				String escaped = escapeImgCss(style);
				e.attr("style", escaped);
			}

			clean.outputSettings(new OutputSettings().escapeMode(EscapeMode.base).prettyPrint(false));
			Element body = clean.body();
			if (keepTextOnly) {
				content = body.text();
			} else {
				content = body.html();
			}
		}
		return content;
	}

	public static String escapeIFrameCss(String orig) {
		String rule = "";
		CSSOMParser parser = new CSSOMParser();
		try {
			List<String> rules = Lists.newArrayList();
			CSSStyleDeclaration decl = parser.parseStyleDeclaration(new InputSource(new StringReader(orig)));

			for (int i = 0; i < decl.getLength(); i++) {
				String property = decl.item(i);
				String value = decl.getPropertyValue(property);
				if (StringUtils.isBlank(property) || StringUtils.isBlank(value)) {
					continue;
				}

				if (ALLOWED_IFRAME_CSS_RULES.contains(property) && StringUtils.containsNone(value, FORBIDDEN_CSS_RULE_CHARACTERS)) {
					rules.add(property + ":" + decl.getPropertyValue(property) + ";");
				}
			}
			rule = StringUtils.join(rules, "");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return rule;
	}

	public static String escapeImgCss(String orig) {
		String rule = "";
		CSSOMParser parser = new CSSOMParser();
		try {
			List<String> rules = Lists.newArrayList();
			CSSStyleDeclaration decl = parser.parseStyleDeclaration(new InputSource(new StringReader(orig)));

			for (int i = 0; i < decl.getLength(); i++) {
				String property = decl.item(i);
				String value = decl.getPropertyValue(property);
				if (StringUtils.isBlank(property) || StringUtils.isBlank(value)) {
					continue;
				}

				if (ALLOWED_IMG_CSS_RULES.contains(property) && StringUtils.containsNone(value, FORBIDDEN_CSS_RULE_CHARACTERS)) {
					rules.add(property + ":" + decl.getPropertyValue(property) + ";");
				}
			}
			rule = StringUtils.join(rules, "");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return rule;
	}


	public static String trimInvalidXmlCharacters(String xml) {
		if (StringUtils.isBlank(xml)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();

		boolean firstTagFound = false;
		for (int i = 0; i < xml.length(); i++) {
			char c = xml.charAt(i);

			if (!firstTagFound) {
				if (c == '<') {
					firstTagFound = true;
				} else {
					continue;
				}
			}

			if (c >= 32 || c == 9 || c == 10 || c == 13) {
				if (!Character.isHighSurrogate(c) && !Character.isLowSurrogate(c)) {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * When there was an error fetching the feed
	 * 
	 */
	public static Date buildDisabledUntil(int errorCount) {
		Date now = new Date();
		int retriesBeforeDisable = 3;

		if (errorCount >= retriesBeforeDisable) {
			int disabledHours = errorCount - retriesBeforeDisable + 1;
			disabledHours = Math.min(24 * 7, disabledHours);
			return DateUtils.addHours(now, disabledHours);
		}
		return now;
	}

	/**
	 * When the feed was refreshed successfully
	 */
	public static Date buildDisabledUntil(Date publishedDate, Long averageEntryInterval, Date defaultRefreshInterval) {
		Date now = new Date();

		if (publishedDate == null) {
			// feed with no entries, recheck in 24 hours
			return DateUtils.addHours(now, 24);
		} else if (publishedDate.before(DateUtils.addMonths(now, -1))) {
			// older than a month, recheck in 24 hours
			return DateUtils.addHours(now, 24);
		} else if (publishedDate.before(DateUtils.addDays(now, -14))) {
			// older than two weeks, recheck in 12 hours
			return DateUtils.addHours(now, 12);
		} else if (publishedDate.before(DateUtils.addDays(now, -7))) {
			// older than a week, recheck in 6 hours
			return DateUtils.addHours(now, 6);
		} else if (averageEntryInterval != null) {
			// use average time between entries to decide when to refresh next, divided by factor
			int factor = 2;

			// not more than 6 hours
			long date = Math.min(DateUtils.addHours(now, 6).getTime(), now.getTime() + averageEntryInterval / factor);

			// not less than default refresh interval
			date = Math.max(defaultRefreshInterval.getTime(), date);

			return new Date(date);
		} else {
			// unknown case, recheck in 24 hours
			return DateUtils.addHours(now, 24);
		}
	}


	public static String removeTrailingSlash(String url) {
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

	public static String toAbsoluteUrl(String url, String baseUrl) {
		url = StringUtils.trimToNull(StringUtils.normalizeSpace(url));
		if (baseUrl == null || url == null || url.startsWith("http")) {
			return url;
		}

		if (url.startsWith("/") == false) {
			url = "/" + url;
		}

		return baseUrl + url;
	}

	public static String proxyImages(String content, String publicUrl, boolean proxyImages) {
		if (!proxyImages) {
			return content;
		}
		if (StringUtils.isBlank(content)) {
			return content;
		}

		Document doc = Jsoup.parse(content);
		Elements elements = doc.select("img");
		for (Element element : elements) {
			String href = element.attr("src");
			if (href != null) {
				String proxy = removeTrailingSlash(publicUrl) + "/rest/server/proxy?u=" + imageProxyEncoder(href);
				element.attr("src", proxy);
			}
		}

		return doc.body().html();
	}

	public static String rot13(String msg) {
		StringBuilder message = new StringBuilder();

		for (char c : msg.toCharArray()) {
			if (c >= 'a' && c <= 'm')
				c += 13;
			else if (c >= 'n' && c <= 'z')
				c -= 13;
			else if (c >= 'A' && c <= 'M')
				c += 13;
			else if (c >= 'N' && c <= 'Z')
				c -= 13;
			message.append(c);
		}

		return message.toString();
	}

	public static String imageProxyEncoder(String url) {
		return Base64.encodeBase64String(rot13(url).getBytes());
	}

	public static String imageProxyDecoder(String code) {
		return rot13(new String(Base64.decodeBase64(code)));
	}

}