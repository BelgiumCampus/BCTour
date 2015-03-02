// Copyright 2007-2014 metaio GmbH. All rights reserved.
package com.metaio.Example;

import java.io.File;
import java.util.concurrent.locks.Lock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.metaio.cloud.plugin.util.MetaioCloudUtils;
import com.metaio.sdk.ARELInterpreterAndroidJava;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.AnnotatedGeometriesGroupCallback;
import com.metaio.sdk.jni.EGEOMETRY_FOCUS_STATE;
import com.metaio.sdk.jni.IAnnotatedGeometriesGroup;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.ImageStruct;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.SensorValues;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

public class TutorialLocationBasedAR extends ARViewActivity
{
	private IAnnotatedGeometriesGroup mAnnotatedGeometriesGroup;

	private MyAnnotatedGeometriesGroupCallback mAnnotatedGeometriesGroupCallback;

	LinearLayout layoutOfPopup; 
	PopupWindow popupMessage; 
	Button popupButton, 
	insidePopupButton; 
	TextView popupText;

	
	/**
	 * Geometries
	 */
	private IGeometry mLamdaGeo;
	private IGeometry mEtaGeo;
	private IGeometry mOmegaGeo;
	private IGeometry mSigmaGeo;
	private IGeometry mGammaGeo;
	private IGeometry mThetaGeo;
	private IGeometry mYpsilonGeo;
	private IGeometry mAlphaGeo;
	private IGeometry mInnovationGeo;
	private IGeometry mZetaGeo;
	private IGeometry mBetaGeo;
	private IGeometry mOminikronGeo;
	private IGeometry mDeltaGeo;

	private IRadar mRadar;

	LLACoordinate lambda;
	LLACoordinate eta ;
	LLACoordinate sigma ;
	LLACoordinate omega ;
	LLACoordinate gamma ;
	LLACoordinate alpha ;
	LLACoordinate theta ;
	LLACoordinate ypsilon ;
	LLACoordinate innovation ;
	LLACoordinate delta;
	LLACoordinate beta ;
	LLACoordinate omnikroin;
	LLACoordinate zeta ;
	
	Databasehandler dbHandler ;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Set GPS tracking configuration
		boolean result = metaioSDK.setTrackingConfiguration("GPS", false);
		MetaioDebug.log("Tracking data loaded: " + result);
		
		dbHandler = new Databasehandler(getApplicationContext());
		
		dbHandler.addContact(new com.metaio.Example.Activity(1,"Lambda", "ORS 221"));
		dbHandler.addContact(new com.metaio.Example.Activity(2,"Gamma", "PRG 121"));
		dbHandler.addContact(new com.metaio.Example.Activity(3,"Omega", "networking 100"));
		dbHandler.addContact(new com.metaio.Example.Activity(4,"Sigma", "Web Development 200"));
		dbHandler.addContact(new com.metaio.Example.Activity(5,"Ypsilon", "Database Development 100(MS ACCESS)"));
		dbHandler.addContact(new com.metaio.Example.Activity(6,"Eta", "Open"));
		dbHandler.addContact(new com.metaio.Example.Activity(7,"Alpha", "Maths 100"));
		dbHandler.addContact(new com.metaio.Example.Activity(8,"Theta", "Database Development 300(Oracle)"));
		dbHandler.addContact(new com.metaio.Example.Activity(9,"Delta", "ORS 221"));
		dbHandler.addContact(new com.metaio.Example.Activity(10,"Beta", "Wed Development 100"));
		dbHandler.addContact(new com.metaio.Example.Activity(11,"Zeta", "Database Development 200(SQL)"));
		dbHandler.addContact(new com.metaio.Example.Activity(12,"Innovation", "In progress"));
		dbHandler.addContact(new com.metaio.Example.Activity(12,"Omikron", "Big 5ive"));

		
	}

	@Override
	protected void onDestroy()
	{
		// Break circular reference of Java objects
		if (mAnnotatedGeometriesGroup != null)
		{
			mAnnotatedGeometriesGroup.registerCallback(null);
		}

		if (mAnnotatedGeometriesGroupCallback != null)
		{
			mAnnotatedGeometriesGroupCallback.delete();
			mAnnotatedGeometriesGroupCallback = null;
		}

		super.onDestroy();
	}
	IGeometry geos[];
	@Override
	public void onDrawFrame()
	{
		if (metaioSDK != null && mSensors != null)
		{
			SensorValues sensorValues = mSensors.getSensorValues();

			float heading = 0.0f;
			if (sensorValues.hasAttitude())
			{
				float m[] = new float[9];
				sensorValues.getAttitude().getRotationMatrix(m);

				Vector3d v = new Vector3d(m[6], m[7], m[8]);
				v.normalize();

				heading = (float)(-Math.atan2(v.getY(), v.getX()) - Math.PI / 2.0);
			}

			geos = new IGeometry[] {mLamdaGeo, mGammaGeo, mOmegaGeo, mSigmaGeo,mThetaGeo,mYpsilonGeo,
					mAlphaGeo,mInnovationGeo,mDeltaGeo,mZetaGeo,mOminikronGeo,mBetaGeo,mEtaGeo};
			Rotation rot = new Rotation((float)(Math.PI / 2.0), 0.0f, -heading);
			for (IGeometry geo : geos)
			{
				if (geo != null)
				{
					geo.setRotation(rot);
				}
			}
		}

		super.onDrawFrame();
	}

	public void onButtonClick(View v)
	{
		finish();
	}

	@Override
	protected int getGUILayout()
	{
		return R.layout.tutorial_location_based_ar;
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler()
	{
		return null;
	}
	
	
	@Override
	protected void loadContents()
	{
		mAnnotatedGeometriesGroup = metaioSDK.createAnnotatedGeometriesGroup();
		mAnnotatedGeometriesGroupCallback = new MyAnnotatedGeometriesGroupCallback();
		mAnnotatedGeometriesGroup.registerCallback(mAnnotatedGeometriesGroupCallback);

		// Clamp geometries' Z position to range [5000;200000] no matter how close or far they are
		// away.
		// This influences minimum and maximum scaling of the geometries (easier for development).
		metaioSDK.setLLAObjectRenderingLimits(5, 200);

		// Set render frustum accordingly
		metaioSDK.setRendererClippingPlaneLimits(10, 220000);

		// let's create LLA objects for known cities

		 lambda = new LLACoordinate(-25.684270874575727, 28.131034774012154, 1359.5, 0);
		 eta = new LLACoordinate(-25.683855, 28.131477, 1333, 0);
		 sigma = new LLACoordinate(-25.683589, 28.131579, 1326, 0);
		 omega = new LLACoordinate(-25.683567800214757, 28.1311529699103528,1376, 0);
		 gamma = new LLACoordinate(-25.68399470190638, 28.131349165318454, 1367.5, 0);
		 alpha = new LLACoordinate(-25.684663194873277, 28.13151448023473, 1383.7, 0);
		 theta = new LLACoordinate(-25.684190717412566,28.131337946802475, 1338.3, 0);
		 ypsilon = new LLACoordinate(-25.683362155805103, 28.13141977200438, 1368.9, 0);
		 innovation = new LLACoordinate(-25.683773, 28.131455, 1337, 0);
		 delta = new LLACoordinate(-25.684277059505767, 28.13131387593448, 1355.0, 0);
		 beta = new LLACoordinate(-25.68423636741511, 28.13128627964048, 1365.4, 0);
		 omnikroin = new LLACoordinate(-25.684283417714443, 28.1311979359364, 1359.7, 0);
		 zeta = new LLACoordinate(-25.68419413946249, 28.131249209970676, 1388.3, 0);
		

		// Load some POIs. Each of them has the same shape at its geoposition. We pass a string
		// (const char*) to IAnnotatedGeometriesGroup::addGeometry so that we can use it as POI
		// title
		// in the callback, in order to create an annotation image with the title on it.
	
		mLamdaGeo = createPOIGeometry(lambda);
		mAnnotatedGeometriesGroup.addGeometry(mLamdaGeo, "Lambda");
		
		mGammaGeo = createPOIGeometry(gamma);
		mAnnotatedGeometriesGroup.addGeometry(mGammaGeo, "Gamma");
		
		mOmegaGeo = createPOIGeometry(omega);
		mAnnotatedGeometriesGroup.addGeometry(mOmegaGeo, "Omega");

		mSigmaGeo = createPOIGeometry(sigma);
		mAnnotatedGeometriesGroup.addGeometry(mSigmaGeo, "Sigma");
		
		mEtaGeo = createPOIGeometry(eta);
		mAnnotatedGeometriesGroup.addGeometry(mEtaGeo, "Eta");
		
		mAlphaGeo = createPOIGeometry(alpha);
		mAnnotatedGeometriesGroup.addGeometry(mAlphaGeo, "Alpha");
		
		mThetaGeo = createPOIGeometry(theta);
		mAnnotatedGeometriesGroup.addGeometry(mThetaGeo, "Theta");
		
		mYpsilonGeo = createPOIGeometry(ypsilon);
		mAnnotatedGeometriesGroup.addGeometry(mYpsilonGeo, "Ypsilon");
		
		mInnovationGeo = createPOIGeometry(innovation);
		mAnnotatedGeometriesGroup.addGeometry(mInnovationGeo, "Innovation Hub");
		
		mBetaGeo = createPOIGeometry(beta);
		mAnnotatedGeometriesGroup.addGeometry(mBetaGeo, "Beta ");
		
		mDeltaGeo = createPOIGeometry(delta);
		mAnnotatedGeometriesGroup.addGeometry(mDeltaGeo, "Delta ");
		
		mZetaGeo = createPOIGeometry(zeta);
		mAnnotatedGeometriesGroup.addGeometry(mZetaGeo, "Zeta ");
		
		mOminikronGeo = createPOIGeometry(omnikroin);
		mAnnotatedGeometriesGroup.addGeometry(mOminikronGeo, "Ominikron");
		
		
		

		// create radar
		mRadar = metaioSDK.createRadar();
		mRadar.setBackgroundTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
				"TutorialLocationBasedAR/Assets/radar.png"));
		mRadar.setObjectsDefaultTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
				"TutorialLocationBasedAR/Assets/TrackingDot.png"));
		mRadar.setRelativeToScreen(IGeometry.ANCHOR_TL);

		// add geometries to the radar

		
		mRadar.add(mLamdaGeo);
		mRadar.add(mEtaGeo);
		mRadar.add(mSigmaGeo);
		mRadar.add(mGammaGeo);
		mRadar.add(mOmegaGeo);
		mRadar.add(mAlphaGeo);
		mRadar.add(mYpsilonGeo);
		mRadar.add(mThetaGeo);
		mRadar.add(mInnovationGeo);	
		mRadar.add(mZetaGeo);	
		mRadar.add(mBetaGeo);	
		mRadar.add(mDeltaGeo);	
		mRadar.add(mOminikronGeo);	
	}

	private IGeometry createPOIGeometry(LLACoordinate lla)
	{
		final File path =
				AssetsManager.getAssetPathAsFile(getApplicationContext(),
						"TutorialLocationBasedAR/Assets/ExamplePOI.obj");
		if (path != null)
		{
			IGeometry geo = metaioSDK.createGeometry(path);
			geo.setTranslationLLA(lla);
			geo.setLLALimitsEnabled(true);
			geo.setScale(100);
			return geo;
		}
		else
		{
			MetaioDebug.log(Log.ERROR, "Missing files for POI geometry");
			return null;
		}
	}
	private PopupWindow pwindo;


	
	private void initiatePopupWindow(IGeometry geo) {
	try {
	// We need to get the instance of the LayoutInflater
	LayoutInflater inflater = (LayoutInflater) TutorialLocationBasedAR.this
	.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.popup_layout,
	(ViewGroup) findViewById(R.id.popup));
	pwindo = new PopupWindow(layout,700,600,true);
	
	com.metaio.Example.Activity activity = null;
	
	
	
	if(geo == mLamdaGeo)
	{
	activity=dbHandler.getActivity("Lambda");
	}
	if(geo.getTranslationLLA().getLatitude() ==gamma.getLatitude())
	{		
		activity=dbHandler.getActivity("Gamma");
	}
	if(geo.getTranslationLLA().getLatitude() ==omega.getLatitude())
	{		
		activity=dbHandler.getActivity("Omega");
	}
	if(geo.getTranslationLLA().getLatitude() ==sigma.getLatitude())
	{		
		activity=dbHandler.getActivity("Sigma");
	}
	if(geo.getTranslationLLA().getLatitude() ==ypsilon.getLatitude())
	{		
		activity=dbHandler.getActivity("Ypsilon");
	}
	if(geo.getTranslationLLA().getLatitude() ==eta.getLatitude())
	{		
		activity=dbHandler.getActivity("Eta");
	}
	 if(geo.getTranslationLLA().getLatitude() ==alpha.getLatitude())
	{		
		 activity=dbHandler.getActivity("Alpha");
	}
	if(geo.getTranslationLLA().getLatitude() ==theta.getLatitude())
	{		
		activity=dbHandler.getActivity("Theta");
	}
	if(geo.getTranslationLLA().getLatitude() ==delta.getLatitude())
	{		
		activity=dbHandler.getActivity("Delta");
	}
	if(geo.getTranslationLLA().getLatitude() ==beta.getLatitude())
	{		
		activity=dbHandler.getActivity("Beta");
	}
	if(geo.getTranslationLLA().getLatitude() ==zeta.getLatitude())
	{		
		activity=dbHandler.getActivity("Zeta");
	}
	 if(geo.getTranslationLLA().getLatitude() ==innovation.getLatitude())
	{		
		 activity=dbHandler.getActivity("Innovation");
	}
	
	((TextView)pwindo.getContentView().findViewById(R.id.txtDetails)).setText(activity.toString());
	
	pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

	Button	btnOk = (Button) layout.findViewById(R.id.close);
	btnOk.setOnClickListener(ok_button_click_listener);
	
	
	
	} catch (Exception e) {
	e.printStackTrace();
	}
	}

	private OnClickListener ok_button_click_listener = new OnClickListener() {
	public void onClick(View v) {
	pwindo.dismiss();

	}
	};


	 
	@Override
	protected void onGeometryTouched(final IGeometry geometry)
	{
		MetaioDebug.log("Geometry selected: " + geometry);
		
		
		initiatePopupWindow(geometry);
		
		
		mSurfaceView.queueEvent(new Runnable()
		{

			@Override
			public void run()
			{
				mRadar.setObjectsDefaultTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
						"TutorialLocationBasedAR/Assets/TrackingDot.png"));
				mRadar.setObjectTexture(geometry, AssetsManager.getAssetPathAsFile(getApplicationContext(),
						"TutorialLocationBasedAR/Assets/red.png"));
				mAnnotatedGeometriesGroup.setSelectedGeometry(geometry);
			
			}
		});
	}

	final class MyAnnotatedGeometriesGroupCallback extends AnnotatedGeometriesGroupCallback
	{
		Bitmap mAnnotationBackground, mEmptyStarImage, mFullStarImage;
		int mAnnotationBackgroundIndex;
		ImageStruct texture;
		String[] textureHash = new String[1];
		TextPaint mPaint;
		Lock geometryLock;


		Bitmap inOutCachedBitmaps[] = new Bitmap[] {mAnnotationBackground, mEmptyStarImage, mFullStarImage};
		int inOutCachedAnnotationBackgroundIndex[] = new int[] {mAnnotationBackgroundIndex};

		public MyAnnotatedGeometriesGroupCallback()
		{
			mPaint = new TextPaint();
			mPaint.setFilterBitmap(true); // enable dithering
			mPaint.setAntiAlias(true); // enable anti-aliasing
		}

		@Override
		public IGeometry loadUpdatedAnnotation(IGeometry geometry, Object userData, IGeometry existingAnnotation)
		{
			if (userData == null)
			{
				return null;
			}

			if (existingAnnotation != null)
			{
				// We don't update the annotation if e.g. distance has changed
				return existingAnnotation;
			}

			String title = (String)userData; // as passed to addGeometry
			LLACoordinate location = geometry.getTranslationLLA();
			float distance = (float)MetaioCloudUtils.getDistanceBetweenTwoCoordinates(location, mSensors.getLocation());
			Bitmap thumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.bclog);
			try
			{
				texture =
						ARELInterpreterAndroidJava.getAnnotationImageForPOI(title, title, distance, "5", thumbnail,
								null,
								metaioSDK.getRenderSize(), TutorialLocationBasedAR.this,
								mPaint, inOutCachedBitmaps, inOutCachedAnnotationBackgroundIndex, textureHash);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (thumbnail != null)
					thumbnail.recycle();
				thumbnail = null;
			}

			mAnnotationBackground = inOutCachedBitmaps[0];
			mEmptyStarImage = inOutCachedBitmaps[1];
			mFullStarImage = inOutCachedBitmaps[2];
			mAnnotationBackgroundIndex = inOutCachedAnnotationBackgroundIndex[0];

			IGeometry resultGeometry = null;

			if (texture != null)
			{
				if (geometryLock != null)
				{
					geometryLock.lock();
				}

				try
				{
					// Use texture "hash" to ensure that SDK loads new texture if texture changed
					resultGeometry = metaioSDK.createGeometryFromImage(textureHash[0], texture, true, false);
				}
				finally
				{
					if (geometryLock != null)
					{
						geometryLock.unlock();
					}
				}
			}

			return resultGeometry;
		}

		@Override
		public void onFocusStateChanged(IGeometry geometry, Object userData, EGEOMETRY_FOCUS_STATE oldState,
				EGEOMETRY_FOCUS_STATE newState)
		{
			MetaioDebug.log("onFocusStateChanged for " + (String)userData + ", " + oldState + "->" + newState);
	
		}
		
		
		
	}
	
}
