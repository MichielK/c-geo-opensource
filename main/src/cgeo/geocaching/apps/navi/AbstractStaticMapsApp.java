package cgeo.geocaching.apps.navi;

import android.content.Context;
import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import cgeo.geocaching.R;
import cgeo.geocaching.activity.ActivityMixin;
import cgeo.geocaching.apps.AbstractApp;
import cgeo.geocaching.models.Geocache;
import cgeo.geocaching.models.ILogable;
import cgeo.geocaching.models.Waypoint;
import cgeo.geocaching.staticmaps.StaticMapsActivity_;
import cgeo.geocaching.staticmaps.StaticMapsProvider;
import cgeo.geocaching.storage.DataStore;

abstract class AbstractStaticMapsApp extends AbstractApp implements CacheNavigationApp, WaypointNavigationApp {
    protected AbstractStaticMapsApp(@NonNull final String name) {
        super(name, null);
    }

    @Override
    public boolean isInstalled() {
        return true;
    }

    @Override
    public boolean isUsableAsDefaultNavigationApp() {
        return false;
    }

    protected static boolean hasStaticMap(@NonNull final Waypoint waypoint) {
        final String geocode = waypoint.getGeocode();
        if (StringUtils.isNotEmpty(geocode) && DataStore.isOffline(geocode, null)) {
            return StaticMapsProvider.hasStaticMapForWaypoint(geocode, waypoint);
        }
        return false;
    }

    protected static void invokeStaticMaps(final Context context, final Geocache cache, final Waypoint waypoint, final boolean download) {
        final ILogable logable = cache != null && !cache.getLists().isEmpty() ? cache : waypoint;
        // If the cache is not stored for offline, cache seems to be null and waypoint may be null too
        if (logable == null || logable.getGeocode() == null) {
            ActivityMixin.showToast(context, getString(R.string.err_detail_no_map_static));
            return;
        }
        final String geocode = StringUtils.upperCase(logable.getGeocode());

        final StaticMapsActivity_.IntentBuilder_ builder = StaticMapsActivity_.intent(context).geocode(geocode).download(download);
        if (waypoint != null) {
            builder.waypointId(waypoint.getId());
        }
        builder.start();
    }
}
