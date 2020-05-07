package identifiedcacheditem.identifiedcacheditem

import io.grpc.StatusRuntimeException
import io.grpc.inprocess.InProcessChannelBuilder
import main.Test
import spock.lang.Shared
import spock.lang.Specification

class ToIsIdentifiedCachedItemImplBaseImplTest extends Specification {

    @Shared
    ToIsIdentifiedCachedItemGrpc.ToIsIdentifiedCachedItemBlockingStub stub

    def setupSpec() {
        Test.before()
        stub = ToIsIdentifiedCachedItemGrpc.newBlockingStub(InProcessChannelBuilder.forName(ToIsIdentifiedCachedItemGrpc.SERVICE_NAME).usePlaintext().build()).withWaitForReady()
    }

    def """Should not allow empty"""() {
        setup:
        def item = NotIdentifiedCachedItem.newBuilder().build()

        when:
        stub.produce(item)

        then:
        thrown StatusRuntimeException
    }
}
