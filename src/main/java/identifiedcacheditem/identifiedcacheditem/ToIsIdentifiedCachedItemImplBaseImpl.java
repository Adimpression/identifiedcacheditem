package identifiedcacheditem.identifiedcacheditem;

import com.google.protobuf.ByteString;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import id.id.IsId;
import identifiedcacheditem.input.IsInput;
import identifiedcacheditem.output.IsOutput;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.logging.Logger;

public class ToIsIdentifiedCachedItemImplBaseImpl extends ToIsIdentifiedCachedItemGrpc.ToIsIdentifiedCachedItemImplBase {

    private final Logger logger;

    private final HazelcastInstance hzInstance;
    private final Map<String, ByteString> general;
    private final ILock lock;


    public ToIsIdentifiedCachedItemImplBaseImpl() {
        logger = Logger.getLogger(getClass().getName());
        logger.info("starting");

        logger.info("starting hazelcast");
        hzInstance = Hazelcast.newHazelcastInstance();
        logger.info("started hazelcast");

        logger.info("starting general map: hazelcast");
        general = hzInstance.getMap("general");
        logger.info("started general map: hazelcast");

        logger.info("starting general lock: hazelcast");
        lock = hzInstance.getLock("general");
        logger.info("started general lock: hazelcast");

        logger.info("started");
    }

    @Override
    public void produce(final NotIdentifiedCachedItem request, final StreamObserver<IsIdentifiedCachedItem> responseObserver) {
        final IsInput isInput;
        final IsId isId;

        if (!request.hasIsInput()) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
        }

        isInput = request.getIsInput();
        if (!isInput.hasIsId()) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
        }

        isId = isInput.getIsId();
        if (!isId.hasIsOutput()) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
        }

        final String isStringValue = isId.getIsOutput()
                .getIsStringValue();

        if (isStringValue.isEmpty()) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("422"));
        }

        responseObserver.onNext(IsIdentifiedCachedItem.newBuilder()
                .setIsOutput(IsOutput.newBuilder()
                        .setIsKnownBoolean(general.containsKey(isStringValue)
                        )
                        .build())
                .build());
        responseObserver.onCompleted();
    }
}
