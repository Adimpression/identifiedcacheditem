package identifiedcacheditem.identifiedcacheditem

import com.google.protobuf.ByteString
import id.id.IsId
import id.output.IsOutput
import identifiedcacheditem.input.IsInput
import io.grpc.StatusRuntimeException
import io.grpc.inprocess.InProcessChannelBuilder
import main.Test
import removedcacheditem.removedcacheditem.NotRemovedCachedItem
import removedcacheditem.removedcacheditem.ToIsRemovedCachedItemGrpc
import spock.lang.Shared
import spock.lang.Specification
import storedcacheditem.storedcacheditem.NotStoredCachedItem
import storedcacheditem.storedcacheditem.ToIsStoredCachedItemGrpc

import java.util.concurrent.TimeUnit

class ToIsIdentifiedCachedItemImplBaseImplTest extends Specification {

    @Shared
    ToIsIdentifiedCachedItemGrpc.ToIsIdentifiedCachedItemBlockingStub stub

    @Shared
    ToIsStoredCachedItemGrpc.ToIsStoredCachedItemBlockingStub toIsStoredCachedItemBlockingStub

    @Shared
    ToIsRemovedCachedItemGrpc.ToIsRemovedCachedItemBlockingStub toIsRemovedCachedItemBlockingStub

    def setupSpec() {
        Test.before()
        stub = ToIsIdentifiedCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsIdentifiedCachedItemGrpc.SERVICE_NAME).usePlaintext().build()).withWaitForReady().withDeadlineAfter(1, TimeUnit.MINUTES)
        toIsStoredCachedItemBlockingStub = ToIsStoredCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsStoredCachedItemGrpc.SERVICE_NAME).usePlaintext().build()).withWaitForReady().withDeadlineAfter(1, TimeUnit.MINUTES)
        toIsRemovedCachedItemBlockingStub = ToIsRemovedCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsRemovedCachedItemGrpc.SERVICE_NAME).usePlaintext().build()).withWaitForReady().withDeadlineAfter(1, TimeUnit.MINUTES)
    }

    def """Should not allow missing input"""() {
        setup:
        def item = NotIdentifiedCachedItem.newBuilder().build()

        when:
        stub.produce(item)

        then:
        thrown StatusRuntimeException
    }

    def """Should not allow empty input"""() {
        setup:
        def item = NotIdentifiedCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder().build())
                .build()

        when:
        stub.produce(item)

        then:
        thrown StatusRuntimeException
    }

    def """Should not allow missing input -> id"""() {
        setup:
        def item = NotIdentifiedCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder().build())
                .build()

        when:
        stub.produce(item)

        then:
        thrown StatusRuntimeException
    }

    def """Should not allow empty input -> id"""() {
        setup:
        def item = NotIdentifiedCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder()
                        .setIsId(IsId.newBuilder().build())
                        .build())
                .build()

        when:
        stub.produce(item)

        then:
        thrown StatusRuntimeException
    }

    def """Should not allow empty input -> id -> output"""() {
        setup:
        def item = NotIdentifiedCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder().build())
                                .build())
                        .build())
                .build()

        when:
        stub.produce(item)

        then:
        thrown StatusRuntimeException
    }

    def """Should not allow empty input -> id -> output -> string value"""() {
        setup:
        def item = NotIdentifiedCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue("")
                                        .build())
                                .build())
                        .build())
                .build()

        when:
        stub.produce(item)

        then:
        thrown StatusRuntimeException
    }

    def """Should succeed on non empty input -> id -> output -> string value"""() {
        setup:
        def item = NotIdentifiedCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(String.valueOf(System.currentTimeMillis()))
                                        .build())
                                .build())
                        .build())
                .build()

        when:
        IsIdentifiedCachedItem isIdentifiedCachedItem = stub.produce(item)

        then:
        assert isIdentifiedCachedItem.hasIsOutput()
        assert !isIdentifiedCachedItem.getIsOutput().getIsKnownBoolean()
    }

    def """Should return known on existing key"""() {
        setup:
        def key = String.valueOf(System.currentTimeMillis())

        def notStoredCachedItem = NotStoredCachedItem.newBuilder()
                .setIsInput(storedcacheditem.input.IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(key)
                                        .build())
                                .build())
                        .setIsUseLockBoolean(true)
                        .setIsItemBytes(ByteString.copyFrom(key, "UTF-8"))
                        .build())
                .build()

        def item = NotIdentifiedCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(key)
                                        .build())
                                .build())
                        .build())
                .build()

        when:
        toIsStoredCachedItemBlockingStub.produce(notStoredCachedItem)
        IsIdentifiedCachedItem isIdentifiedCachedItem = stub.produce(item)

        then:
        assert isIdentifiedCachedItem.hasIsOutput()
        assert isIdentifiedCachedItem.getIsOutput().getIsKnownBoolean()
    }

    def """Should return not known on non existing key"""() {
        setup:
        def key = String.valueOf(System.currentTimeMillis())

        def notStoredCachedItem = NotStoredCachedItem.newBuilder()
                .setIsInput(storedcacheditem.input.IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(key)
                                        .build())
                                .build())
                        .setIsUseLockBoolean(true)
                        .setIsItemBytes(ByteString.copyFrom(key, "UTF-8"))
                        .build())
                .build()

        def notRemovedCachedItem = NotRemovedCachedItem.newBuilder()
                .setIsInput(removedcacheditem.input.IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(key)
                                        .build())
                                .build())
                        .setIsUseLockBoolean(true)
                        .build())
                .build()

        def item = NotIdentifiedCachedItem.newBuilder()
                .setIsInput(IsInput.newBuilder()
                        .setIsId(IsId.newBuilder()
                                .setIsOutput(IsOutput.newBuilder()
                                        .setIsStringValue(key)
                                        .build())
                                .build())
                        .build())
                .build()

        when:
        toIsStoredCachedItemBlockingStub.produce(notStoredCachedItem)
        toIsRemovedCachedItemBlockingStub.produce(notRemovedCachedItem)
        IsIdentifiedCachedItem isIdentifiedCachedItem = stub.produce(item)

        then:
        assert isIdentifiedCachedItem.hasIsOutput()
        assert !isIdentifiedCachedItem.getIsOutput().getIsKnownBoolean()
    }
}
