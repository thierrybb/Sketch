package ca.etsmtl.sketch;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.builder.RemoteBusBuilder;
import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.event.OnInkDrawingAdded;

public class ClientTestLauncher {
    public static void main(String[] args) throws IOException {
//        RemoteBusBuilder remoteBusBuilder = new RemoteBusBuilder();
//        EventBus bus = remoteBusBuilder.setServerHostname("127.0.0.1").setServerPort(1111).build();
//
//        bus.register(new EventBus.EventListener() {
//            @Override
//            public void onEventReceived(Event event) {
//                System.out.println("OnStrokeAdded : " + event.getClass().getName());
//            }
//        }, OnInkDrawingAdded.class);
//
//        bus.post(new OnInkDrawingAdded());
    }
}
