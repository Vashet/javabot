package milo

import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.glassfish.grizzly.Buffer
import java.lang.String

class IrcFilter extends BaseFilter {

  override def handleRead(ctx: FilterChainContext): NextAction = {
    System.out.println("handleRead")
    val message: Buffer = ctx.getMessage.asInstanceOf[Buffer];
    val stringContent: String = message.toStringContent
    System.out.println("message = " + message);
    System.out.println("stringContent = " + stringContent);
    return super.handleConnect(ctx);
  }

}
