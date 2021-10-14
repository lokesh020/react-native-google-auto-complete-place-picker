// GoogleAutoCompletePlacePickerModule.java

package com.reactlibrary;

import static android.app.Activity.RESULT_CANCELED;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class GoogleAutoCompletePlacePickerModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String E_USER_CANCELLED = "E_USER_CANCELLED";
    private static final String E_UNKNOWN_ERROR_OCCURRED = "E_UNKNOWN_ERROR_OCCURRED";


    private Promise mPromise;
    GoogleAutoCompletePlacePickerModule(ReactApplicationContext context) {
        super(context);
        context.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "GoogleAutoCompletePlacePicker";
    }

    @ReactMethod
    public void pickPlace(final Promise promise) {

        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }
        mPromise = promise;
        ApplicationInfo applicationInfo=null;
        try{
         applicationInfo = getReactApplicationContext().getPackageManager().getApplicationInfo(getReactApplicationContext().getPackageName(),PackageManager.GET_META_DATA);
        }catch (Exception e){
            e.printStackTrace();
        }
        String placeApiKey = applicationInfo.metaData.getString("com.google.android.places.PLACE_API_KEY");
        Places.initialize(getReactApplicationContext(), placeApiKey);
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getReactApplicationContext());

        List<Place.Field> fields = Arrays.asList(Place.Field.ID,Place.Field.ADDRESS_COMPONENTS, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(getReactApplicationContext());
        getReactApplicationContext().startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE, null);

    }


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        try{
            if(mPromise!=null){
                if (resultCode == -1 && requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                    Place place = Autocomplete.getPlaceFromIntent(data);

                    Log.i("√", "onPlaceSelected: " + place.getAddressComponents() + ", " + place.getId() + ", " + place.getAddress() + ", " + place.getLatLng());

                    mPromise.resolve(getPlacePickerData(place));
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i("status", status.getStatusMessage());
                    mPromise.reject(E_UNKNOWN_ERROR_OCCURRED, "Unknown error occurred");
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                    mPromise.reject(E_USER_CANCELLED, "Cancelled by user");
                }
                return;
            }
            mPromise=null;
        }catch (Exception e){
            System.out.println("error is ");
        }


    }


    private WritableMap getPlacePickerData(Place place){
        WritableMap params = Arguments.createMap();
        params.putString("name", place.getName());
        params.putString("placeID", place.getId());
        params.putString("formattedAddress", place.getAddress());
        WritableMap coordinate = Arguments.createMap();
        coordinate.putDouble("latitude",place.getLatLng().latitude);
        coordinate.putDouble("longitude",place.getLatLng().longitude);
        params.putMap("coordinate", coordinate);
        return  params;
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}