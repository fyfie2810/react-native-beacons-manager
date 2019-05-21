package estimote;

import android.content.Context;
import android.util.Log;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EstimoteProximity {

  private static final String LOG_TAG = "EstimoteProximity";
  private static final String APP_ID = "abc";
  private static final String APP_TOKEN = "abc";

  private EstimoteCloudCredentials cloudCredentials;
  private ProximityObserver proximityObserver;
  private ArrayList<ProximityObserver.Handler> observationHandlerList;


  public EstimoteProximity(Context context){
    cloudCredentials = new EstimoteCloudCredentials(APP_ID, APP_TOKEN);
    observationHandlerList = new ArrayList<>();
    proximityObserver =
      new ProximityObserverBuilder(context, cloudCredentials)
        .onError(new Function1<Throwable, Unit>() {
          @Override
          public Unit invoke(Throwable throwable) {
            Log.d(LOG_TAG, "proximity observer error: " + throwable);
            return null;
          }
        })
        .withBalancedPowerMode()
        .build();

  }

  public ProximityZone createProximityZone(String tag, long range){
    ProximityZone zone = new ProximityZoneBuilder()
      .forTag(tag)
      .inCustomRange(range)

      .onEnter(new Function1<ProximityZoneContext, Unit>() {
        @Override
        public Unit invoke(ProximityZoneContext context) {
          String secretField = context.getAttachments().get("secretField");
          Log.d(LOG_TAG, "What secret <" + secretField + ">");
          return null;
        }
      })

      .onExit(new Function1<ProximityZoneContext, Unit>() {
        @Override
        public Unit invoke(ProximityZoneContext context) {
          Log.d(LOG_TAG, "Bye bye, come again!");
          return null;
        }
      })
      .build();
    return zone;
  }


  public void startObserving(ProximityZone zone){
    ProximityObserver.Handler handler = proximityObserver.startObserving(zone);
    this.observationHandlerList.add(handler);
    Log.d(LOG_TAG, "Start observing");
  }

  public void stopAllObserving(){
    if (this.observationHandlerList!=null) {
      for(ProximityObserver.Handler handler : this.observationHandlerList){
        handler.stop();
        this.observationHandlerList.remove(handler);
        Log.d(LOG_TAG, "Stop observing");
      }
    }
  }

}
