function ObservableArray(items) {
	var _self = this,
		_array = [],
        _handlers = {
	        itemadded: []
        };

	function raiseEvent(event) {
	    _handlers[event.type].forEach(function(h) {
	        h.call(_self, event);
        });
    }

    Object.defineProperty(_self, "addEventListener", {
        configurable: false,
        enumerable: false,
        value: function(eventName, handler) {
            eventName = ("" + eventName).toLowerCase();
            if (!(eventName in _handlers)) throw new Error("Invalid event name.");
            if (typeof handler !== "function") throw new Error("Invalid handler.");
            _handlers[eventName].push(handler);
        }
    });

    Object.defineProperty(_self, "removeEventListener", {
        configurable: false,
        enumerable: false,
        writable: false,
        value: function(eventName, handler) {
            eventName = ("" + eventName).toLowerCase();
            if (!(eventName in _handlers)) throw new Error("Invalid event name.");
            if (typeof handler !== "function") throw new Error("Invalid handler.");
            var h = _handlers[eventName];
            var ln = h.length;
            while (--ln >= 0) {
                if (h[ln] === handler) {
                    h.splice(ln, 1);
                }
            }
        }
    });

    Object.defineProperty(_self, "push", {
        configurable: false,
        enumerable: false,
        writable: false,
        value: function() {
            var index;
            for (var i=0, ln = arguments.length; i<ln; i++) {
                index = _array.length;
                _array.push(arguments[i]);
                raiseEvent({
                    type: "itemadded",
                    index: index,
                    item: arguments[i]
                });
            }
            return _array.length;
        }
    });

    Object.defineProperty(_self, "pop", {
        configurable: false,
        enumerable: false,
        writable: false,
        value: function() {
            if (_array.length > -1) {
                var index = _array.length - 1,
                    item = _array.pop();
                delete _self[index];
                return item;
            }
        }
    });
}

(function test() {
    var x = new ObservableArray(["http://uri.org/1", "http//uri.org/2", "http://uri.org/3",
        "http://uri.org/4"]);

    // console.log("original array: %o", x.slice());

    x.addEventListener("itemadded", function(e) {
        console.log("URI to be queried: %o", e.item);
    });

    console.log("pushing new URI");
    x.push("http://uri.org/5");
    console.log("popping the URI");
    x.pop("http://uri.org/5");
})();