arel.sceneReady(function()
{
	console.log("sceneReady");

	//set a listener to tracking to get information about when the image is tracked
	arel.Events.setListener(arel.Scene, function(type, param){trackingHandler(type, param);});
	
	//enable advanced rendering
	arel.Scene.autoEnableAdvancedRenderingFeatures(function callback(success) {
		if(!success) {
			alert("Advanced rendering features disabled. This device does not support the advanced rendering features of the metaio SDK.");
		} 
	});
	
	//adjust effects
	arel.Scene.setDepthOfFieldParameters(0.1, 0.6, 0.2);
	//slightly reduce amount of motion blur
	arel.Scene.setMotionBlurIntensity(0.8);
});

function trackingHandler(type, param)
{
	//check if there is tracking information available
	if(param[0] !== undefined)
	{
		//if the pattern is found
		if(type && type == arel.Events.Scene.ONTRACKING && param[0].getState() == arel.Tracking.STATE_TRACKING)
		{
			console.log("Tracking is active");
		}
		//if the pattern is lost tracking
		else if(type && type == arel.Events.Scene.ONTRACKING && param[0].getState() == arel.Tracking.STATE_NOTTRACKING)
		{
			console.log("Tracking is lost");
		}
	}
};