package estimote;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.observation.region.beacon.SecureBeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;


public class EstimoteManager  {
  private static final String LOG_TAG = "EstimoteManagerBeacon";
  private BeaconManager beaconManager;
  private ServiceReadyListener serviceReadyListener;
  private BeaconDiscoveredListener beaconDiscoveredListener;

  public interface ServiceReadyListener {
    void onServiceReady();
  }

  public interface BeaconDiscoveredListener {
    void onBeaconDiscovered(Collection<Beacon> beacons, BeaconRegion beaconRegion);
  }


  public EstimoteManager(final Context context){
    final EstimoteManager self = this;
    EstimoteSDK.initialize(context, "compasscheckpoint-hcc", "0d56a23ca6cfbf0d0b75c91d3e53edca");
    runOnUiThread(new Runnable(){
      @Override
      public void run() {
        beaconManager = new BeaconManager(context);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
          @Override
          public void onServiceReady() {
            if(self.serviceReadyListener != null){
              self.serviceReadyListener.onServiceReady();
            }
          }
        });

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
          @Override
          public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
            Log.v(LOG_TAG, "beacon discovered" + beacons.size());
            if(self.beaconDiscoveredListener != null){
              self.beaconDiscoveredListener.onBeaconDiscovered(beacons, beaconRegion);
            }
          }
        });

        Log.d(LOG_TAG, "create estimoted inside run");
      }
    });
    Log.d(LOG_TAG, "create estimoted after run");
  }



  public BeaconRegion createRegion(String identifier, String uuid){
    BeaconRegion beaconRegion;

    if (uuid != null && uuid != "") {
      beaconRegion = new BeaconRegion(identifier, UUID.fromString(uuid), null, null);
    }else{
      beaconRegion = new BeaconRegion(identifier, null, null, null);
    }
    return beaconRegion;
  }

  public SecureBeaconRegion createSecureRegion(String identifier, String uuid){
    SecureBeaconRegion secureBeaconRegion;
    if (uuid != null && uuid != "") {
      secureBeaconRegion = new SecureBeaconRegion(identifier, UUID.fromString(uuid), null, null);
    }else{
      secureBeaconRegion = new SecureBeaconRegion(identifier, null, null, null);
    }
    return secureBeaconRegion;
  }



  public void startRanging(BeaconRegion beaconRegion){
    if (beaconManager!= null) {
      beaconManager.startRanging(beaconRegion);
    }
  }

  public void stopRanging(BeaconRegion beaconRegion){
    if (beaconManager!= null) {
      beaconManager.stopRanging(beaconRegion);
    }
  }

  public void setBackgroundScanPeriod(long period){
    if (beaconManager!= null) {
      beaconManager.setBackgroundScanPeriod(period, period);
    }
  }

  public void setServiceReadyListener(ServiceReadyListener serviceReady){
    this.serviceReadyListener = serviceReady;
  }


  public void setBeaconDiscoveredListener(final BeaconDiscoveredListener beaconDiscovered){
    this.beaconDiscoveredListener = beaconDiscovered;
  }

}


