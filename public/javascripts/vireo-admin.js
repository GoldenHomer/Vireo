/**
 * Common javascript routens that are shared between pages. 
 * 
 * Everything in this file *must* have qunit tests for verification.
 */






/**
 * General function to display an alert message.
 * 
 * If this alert has never been displayed before then a new alert box will be
 * appended to the alert-area. If the alert has been displayed before then it's
 * contents is replaced with the new error message.
 *
 * id: Unique identifier for this error message. It will be used later for clearing the error.
 * heading: The text that should be bolded in the error message.
 * message: A detailed description of the error.
 */
function displayAlert(id, heading,message) {
	 
	 var alert = jQuery("<div id='"+id+"' class='alert alert-error'><button data-dismiss='alert' class='close' type='button'>×</button><p><strong>"+heading+"</strong>: "+message+"</p></div>");
	 
	 if (jQuery("#alert-area #"+id).length == 0) {
		 // This is a new alert, that has never been seen before
		 alert.appendTo(jQuery("#alert-area")).fadeIn();
		 
	 } else {
		 // An allert of this id allready, exists. Replace it.
		jQuery("#alert-area #"+id).replaceWith(alert);
	 }
}

/**
 * General function to clear out stale alert messages.
 */
function clearAlert(id) {
    jQuery("#alert-area #"+id).fadeOut().remove();
}