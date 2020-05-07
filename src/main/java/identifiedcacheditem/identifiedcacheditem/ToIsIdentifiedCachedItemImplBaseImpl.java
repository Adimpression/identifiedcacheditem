package identifiedcacheditem.identifiedcacheditem;

import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class ToIsIdentifiedCachedItemImplBaseImpl extends ToIsIdentifiedCachedItemGrpc.ToIsIdentifiedCachedItemImplBase {

    private final Logger log;

    public ToIsIdentifiedCachedItemImplBaseImpl() {
        log = Logger.getLogger(getClass().getName());
        log.info("starting");

        log.info("started");
    }

    @Override
    public void produce(final NotIdentifiedCachedItem request, final StreamObserver<IsIdentifiedCachedItem> responseObserver) {
        responseObserver.onNext(IsIdentifiedCachedItem.newBuilder()
                .build());
        responseObserver.onCompleted();
    }
}
