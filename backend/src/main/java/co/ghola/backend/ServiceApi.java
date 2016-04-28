package co.ghola.backend;

/**
 * Created by gholadr on 4/28/16.
 */

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;

import java.net.URI;
import java.util.logging.Logger;


@Api(name="aqi",version="v1", description="An API to manage the famous MQTT broker demo app",namespace = @ApiNamespace(ownerDomain = "backend.ghola.co", ownerName = "backend.ghola.co", packagePath=""))
public class ServiceApi {

    private static final Logger log = Logger.getLogger(ServiceApi.class.getName());

    @ApiMethod(name = "publish")
    public void publish(@Nullable @Named("broker") String broker, @Nullable @Named("topic") String topic ,@Named("message") String message ){


        //return true;
    }


}
