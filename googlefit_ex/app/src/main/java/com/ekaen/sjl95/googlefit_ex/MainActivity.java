package com.ekaen.sjl95.googlefit_ex;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.concurrent.TimeUnit;



public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.CONFIG_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .useDefaultAccount()
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {


                                //Async To fetch steps
                                new FetchStepsAsync().execute();

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                }
                            }
                        }
                ).addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            MainActivity.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        authInProgress = true;
                                        result.startResolutionForResult(MainActivity.this, REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                    }
                                }
                            }
                        }
                ).build();
        mClient.connect();
    }
    private class FetchStepsAsync extends AsyncTask<Object, Object, Long> {
        protected Long doInBackground(Object... params) {
            long total = 0;
            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                if (totalSet != null) {
                    total = totalSet.isEmpty()
                            ? 0
                            : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                }
            } else {
                Log.w("api", "There was a problem getting the step count.");
            }
            return total;
        }


        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        final TextView textView=findViewById(R.id.textview);
                    textView.setText(String.valueOf(aLong) );
            //Total steps covered for that day

        }
    }
    
//    원본 코드 (daily가 아니라 누적 걸음수만 보여짐)    
//    private static final int REQUEST_OAUTH = 1;
//    private static final String AUTH_PENDING = "auth_state_pending";
//    private boolean authInProgress = false;
//    private GoogleApiClient mClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        if (savedInstanceState != null) {
//            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
//        }
//
//        mClient = new GoogleApiClient.Builder(this)
//                .addApi(Fitness.SENSORS_API)
//                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mClient.connect();
//    }
//
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
//                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
//                .setDataSourceTypes(DataSource.TYPE_RAW)
//                .build();
//
//        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
//            @Override
//            public void onResult(DataSourcesResult dataSourcesResult) {
//                for (DataSource dataSource : dataSourcesResult.getDataSources()) {
//                    if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(dataSource.getDataType())) {
//                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
//                    }
//                }
//            }
//        };
//
//        Fitness.SensorsApi.findDataSources(mClient, dataSourceRequest)
//                .setResultCallback(dataSourcesResultCallback);
//    }
//
//    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
//
//        SensorRequest request = new SensorRequest.Builder()
//                .setDataSource(dataSource)
//                .setDataType(dataType)
//                .setSamplingRate(3, TimeUnit.SECONDS)
//                .build();
//
//        Fitness.SensorsApi.add(mClient, request, this)
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.isSuccess()) {
//                            Log.e("GoogleFit", "SensorApi successfully added");
//                        }
//                    }
//                });
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        if (!authInProgress) {
//            try {
//                authInProgress = true;
//                connectionResult.startResolutionForResult(MainActivity.this, REQUEST_OAUTH);
//            } catch (IntentSender.SendIntentException e) {
//
//            }
//        } else {
//            Log.e("GoogleFit", "authInProgress");
//        }
//    }
//
//
//
//    @Override
//    public void onDataPoint(DataPoint dataPoint) {
//
//        final TextView textView=findViewById(R.id.textview);
//
//        for (final Field field : dataPoint.getDataType().getFields()) {
//            final Value value = dataPoint.getValue(field);
//            runOnUiThread(new Runnable() {
//
//                String a = String.valueOf(value);
//                int result = Integer.parseInt(a)-32735;
//                @Override
//                public void run() {
//                    textView.setText(field.getName() + result );
//                    //Toast.makeText(getApplicationContext(),
//                    //  "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_OAUTH) {
//            authInProgress = false;
//            if (resultCode == RESULT_OK) {
//                if (!mClient.isConnecting() && !mClient.isConnected()) {
//                    mClient.connect();
//                }
//            } else if (resultCode == RESULT_CANCELED) {
//                Log.e("GoogleFit", "RESULT_CANCELED");
//            }
//        } else {
//            Log.e("GoogleFit", "requestCode NOT request_oauth");
//        }
//    }
}
