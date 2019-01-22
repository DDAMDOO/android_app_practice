package com.ekaen.sjl95.googlefit_ex;

import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes( DataType.TYPE_STEP_COUNT_CUMULATIVE )
                .setDataSourceTypes( DataSource.TYPE_RAW )
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType() ) ) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {

        SensorRequest request = new SensorRequest.Builder()
                .setDataSource( dataSource )
                .setDataType( dataType )
                .setSamplingRate( 3, TimeUnit.SECONDS )
                .build();

        Fitness.SensorsApi.add( mApiClient, request, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e( "GoogleFit", "SensorApi successfully added" );
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if( !authInProgress ) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( MainActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }
    }


    @Override
    public void onDataPoint(DataPoint dataPoint) {
        for( final Field field : dataPoint.getDataType().getFields() ) {
            final Value value = dataPoint.getValue( field );
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_OAUTH ) {
            authInProgress = false;
            if( resultCode == RESULT_OK ) {
                if( !mApiClient.isConnecting() && !mApiClient.isConnected() ) {
                    mApiClient.connect();
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "RESULT_CANCELED" );
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }
}


//public class MainActivity extends AppCompatActivity {
//    final static String TAG="TAG ";
//    final int REQUEST_OAUTH_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;;
//    private GoogleApiClient googleApiClient = null;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // 필요한 권한들 정의
//        FitnessOptions fitnessOptions =
//                FitnessOptions.builder()
//                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
//                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                        .build();
//        if(!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
//            GoogleSignIn.requestPermissions(
//                    this,
//                    REQUEST_OAUTH_REQUEST_CODE,
//                    GoogleSignIn.getLastSignedInAccount(this),
//                    fitnessOptions);
//        } else {
//            Fitness.getRecordingClient(this,
//                    GoogleSignIn.getLastSignedInAccount(this))
//                    .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
//                    .addOnCompleteListener(
//                            new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.i(TAG, "Successfully subscribed!");
//                                    } else {
//                                        Log.w(TAG, "There was a problem subscribing.", task.getException());
//                                    }
//                                }
//                            });
//        }
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // 로그인 성공시
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
//                Fitness.getRecordingClient(this,GoogleSignIn.getLastSignedInAccount(this)).subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE);
//                readData();
//            }
//        }
//    }
//
//    private void readData() {
//        final Calendar cal = Calendar.getInstance();
//        Date now = Calendar.getInstance().getTime();
//        cal.setTime(now);
//
//        // 시작 시간
//        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH), 6, 0, 0);
//        long startTime = cal.getTimeInMillis();
//
//        // 종료 시간
//        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH), 22, 0, 0);
//        long endTime = cal.getTimeInMillis();
//
//        Fitness.getHistoryClient(this,
//                GoogleSignIn.getLastSignedInAccount(this))
//                .readData(new DataReadRequest.Builder()
//                        .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw 걸음 수
//                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//                        .build())
//                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
//                    @Override
//                    public void onSuccess(DataReadResponse response) {
//                        DataSet dataSet = response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);
//                        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
//
//                        for (DataPoint dp : dataSet.getDataPoints()) {
//                            Log.i(TAG, "Data point:");
//                            Log.i(TAG, "\tType: " + dp.getDataType().getName());
//
//                            Log.i(TAG, "\tStart: " + dp.getStartTime(TimeUnit.MILLISECONDS));
//                            Log.i(TAG, "\tEnd: " + dp.getEndTime(TimeUnit.MILLISECONDS));
//                            for (Field field : dp.getDataType().getFields()) {
//                                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
//                            }
//                        }
//                    }
//                });
//    }
//}
