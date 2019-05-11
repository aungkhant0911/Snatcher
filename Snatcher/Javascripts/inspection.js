var isFeatureOn = false;
var element = null;
var color = null;
document.addEventListener('keypress', OnOffFeature)
document.addEventListener('keypress', deleteElement)

function OnOffFeature(e) {
	if (e.key === "q") {		
		isFeatureOn = !isFeatureOn;

		if (isFeatureOn) {
			document.addEventListener('mousemove', highlightElement);
			
		} else {
			document.removeEventListener('mousemove', highlightElement);
			if (color !== null)
				element.style.backgroundColor = color;
		}
	} 
}

function deleteElement(e) {
	if(isFeatureOn && element != null && e.key === "e")
		element.remove();
}

function highlightElement(e) {
	var elements = document.querySelectorAll( ":hover" );	
	var tmp_element = elements[elements.length - 1];
	
	if(element != null) {
		element.style.backgroundColor = color;
	}
	
	element = tmp_element;
	color = tmp_element.style.backgroundColor;	
	element.style.backgroundColor = "#FDFF47";
	shake(element);
}
