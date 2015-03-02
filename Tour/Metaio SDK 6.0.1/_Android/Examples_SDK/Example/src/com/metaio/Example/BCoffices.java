package com.metaio.Example;

import java.io.File;
import java.util.concurrent.locks.Lock;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

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

public class BCoffices extends ARViewActivity{

	private IAnnotatedGeometriesGroup mAnnotatedGeometriesGroup;

	private MyAnnotatedGeometriesGroupCallback mAnnotatedGeometriesGroupCallback;

	/**
	 * Geometries
	 */
	private IGeometry mAdminOffice;
	private IGeometry mCounsellorsOficce;
	private IGeometry mSecurityOffice;
	private IGeometry mMarketingDepartment;
	private IGeometry mEducaticationDepartment;

	private IRadar mRadar;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Set GPS tracking configuration
		boolean result = metaioSDK.setTrackingConfiguration("GPS", false);
		MetaioDebug.log("Tracking data loaded: " + result);
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

			IGeometry geos[] = new IGeometry[] {mAdminOffice, mCounsellorsOficce, mSecurityOffice,mMarketingDepartment,mEducaticationDepartment};
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

		LLACoordinate admin = new LLACoordinate(-25.68461148662922, 28.13083541639044, 1356.5, 12.00);
		LLACoordinate counsellors = new LLACoordinate(-25.683872141044148, 28.13117784244522304, 1366.6, 0);
		LLACoordinate security = new LLACoordinate(-25.68326, 28.131182,1328, 0);
		LLACoordinate marketing = new LLACoordinate(-25.68424528419459, 28.130888469909266,1360.7, 0);
		LLACoordinate education = new LLACoordinate(-25.68443993571928, 28.13122722717626,1357.6, 0);
		

		// Load some POIs. Each of them has the same shape at its geoposition. We pass a string
		// (const char*) to IAnnotatedGeometriesGroup::addGeometry so that we can use it as POI
		// title
		// in the callback, in order to create an annotation image with the title on it.
	
		mAdminOffice = createPOIGeometry(admin);
		mAnnotatedGeometriesGroup.addGeometry(mAdminOffice, "Admin Office ");
		mCounsellorsOficce = createPOIGeometry(counsellors);
		mAnnotatedGeometriesGroup.addGeometry(mCounsellorsOficce, "Student Counselor Office");
		mSecurityOffice = createPOIGeometry(security);
		mAnnotatedGeometriesGroup.addGeometry(mSecurityOffice, "Security Office");
		mMarketingDepartment = createPOIGeometry(marketing);
		mAnnotatedGeometriesGroup.addGeometry(mMarketingDepartment, "Marketing Department");
		mEducaticationDepartment = createPOIGeometry(education);
		mAnnotatedGeometriesGroup.addGeometry(mEducaticationDepartment, "Education Department");
		

		// create radar
		mRadar = metaioSDK.createRadar();
		mRadar.setBackgroundTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
				"TutorialLocationBasedAR/Assets/radar.png"));
		mRadar.setObjectsDefaultTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
				"TutorialLocationBasedAR/Assets/OrangeDot.png"));
		mRadar.setRelativeToScreen(IGeometry.ANCHOR_TL);

		// add geometries to the radar

		mRadar.add(mAdminOffice);
		mRadar.add(mCounsellorsOficce);
		mRadar.add(mSecurityOffice);
		mRadar.add(mMarketingDepartment);
		mRadar.add(mEducaticationDepartment);
	}

	private IGeometry createPOIGeometry(LLACoordinate lla)
	{
		final File path =
				AssetsManager.getAssetPathAsFile(getApplicationContext(),
						"TutorialLocationBasedAR/Assets/otherPOI.obj");
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

	@Override
	protected void onGeometryTouched(final IGeometry geometry)
	{
		MetaioDebug.log("Geometry selected: " + geometry);

		mSurfaceView.queueEvent(new Runnable()
		{

			@Override
			public void run()
			{
				mRadar.setObjectsDefaultTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
						"TutorialLocationBasedAR/Assets/OrangeDot.png"));
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
								metaioSDK.getRenderSize(), BCoffices.this,
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
