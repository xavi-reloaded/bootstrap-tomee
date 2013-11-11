var module = angular.module('commafeed.directives', []);

module.directive('focus', ['$timeout', function($timeout) {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			scope.$watch(attrs.focus, function(value) {
				if (!value)
					return;
				$timeout(function() {
					$(element).focus();
				});
			});
		}
	};
}]);

/**
 * Open a popup window pointing to the url in the href attribute
 */
module.directive('popup', function() {
	var opts = 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=800';
	return {
		link : function(scope, elm, attrs) {
			elm.bind('click', function(event) {
				window.open(this.href, '', opts);
				event.preventDefault();
			});
		}
	};
});

/**
 * Reusable favicon component
 */
module.directive('favicon', function() {
	var tpl = '<img ng-src="{{url}}" class="favicon"></img>';
	return {
		restrict : 'E',
		scope : {
			url : '='
		},
		replace : true,
		template : tpl
	};
});

/**
 * Support for the blur event
 */
module.directive('ngBlur', function() {
	return {
		restrict : 'A',
		link : function(scope, elm, attrs) {
			elm.bind('blur', function() {
				scope.$apply(attrs.ngBlur);
			});
		}
	};
});

/**
 * Prevent mousewheel scrolling from propagating to the parent when scrollbar
 * reaches top or bottom
 */
module.directive('mousewheelScrolling', function() {
	return {
		restrict : 'A',
		link : function(scope, elem, attr) {
			elem.bind('mousewheel', function(e, d) {
				var t = $(this);
				if (d > 0 && t.scrollTop() === 0) {
					e.preventDefault();
				} else {
					if (d < 0 && (t.scrollTop() == t.get(0).scrollHeight - t.innerHeight())) {
						e.preventDefault();
					}
				}
			});
		}
	};
});

/**
 * Needed to use recursive directives. Wrap a recursive element with a
 * <recursive> tag
 */
module.directive('recursive', ['$compile', function($compile) {
	return {
		restrict : 'E',
		priority : 100000,
		compile : function(tElement, tAttr) {
			var contents = tElement.contents().remove();
			var compiledContents = null;
			return function(scope, iElement, iAttr) {
				if (!compiledContents) {
					compiledContents = $compile(contents);
				}
				iElement.append(compiledContents(scope, function(clone) {
					return clone;
				}));
			};
		}
	};
}]);

/**
 * Reusable spinner component
 */
module.directive('spinner', function() {
	return {
		scope : {
			shown : '='
		},
		restrict : 'A',
		link : function($scope, element) {
			element.addClass('spinner');
			var opts = {
				lines : 11, // The number of lines to draw
				length : 5, // The length of each line
				width : 3, // The line thickness
				radius : 8, // The radius of the inner circle
				corners : 1, // Corner roundness (0..1)
				rotate : 0, // The rotation offset
				color : '#000', // #rgb or #rrggbb
				speed : 1.3, // Rounds per second
				trail : 60, // Afterglow percentage
				shadow : false, // Whether to render a shadow
				hwaccel : true, // Whether to use hardware acceleration
				zIndex : 2e9, // The z-index (defaults to 2000000000)
				top : 'auto', // Top position relative to parent in px
				left : 'auto' // Left position relative to parent in px
			};
			var spinner = new Spinner(opts);
			$scope.$watch('shown', function(shown) {
				if (shown) {
					spinner.spin();
					element.append(spinner.el);
				} else {
					spinner.stop();
				}
			});
		}
	};
});

module.directive('draggable', function() {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			element.draggable({
				revert : 'invalid',
				helper : 'clone',
				distance : 10,
				axis : 'y'
			}).data('source', scope.$eval(attrs.draggable));
		}
	};
});

module.directive('droppable', ['CategoryService', 'FeedService', function(CategoryService, FeedService) {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			element.droppable({
				tolerance : 'pointer',
				over : function(event, ui) {

				},
				drop : function(event, ui) {
					var draggable = angular.element(ui.draggable);

					var source = draggable.data('source');
					var target = scope.$eval(attrs.droppable);

					if (angular.equals(source, target)) {
						return;
					}

					var data = {
						id : source.id,
						name : source.name
					};

					if (source.children) {
						// source is a category

					} else {
						// source is a feed

						if (target.children) {
							// target is a category
							data.categoryId = target.id;
							data.position = 0;
						} else {
							// target is a feed
							data.categoryId = target.categoryId;
							data.position = target.position;
						}

						FeedService.modify(data, function() {
							CategoryService.init();
						});
					}
					scope.$apply();
				}
			});
		}
	};
}]);

module.directive('metricMeter', function() {
	return {
		scope : {
			metric : '=',
			label : '='
		},
		restrict : 'E',
		templateUrl : 'templates/_metrics.meter.html'
	};
});

module.directive('metricGauge', function() {
	return {
		scope : {
			metric : '=',
			label : '='
		},
		restrict : 'E',
		templateUrl : 'templates/_metrics.gauge.html'
	};
});