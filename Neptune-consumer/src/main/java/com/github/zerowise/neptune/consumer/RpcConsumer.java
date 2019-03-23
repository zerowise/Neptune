package com.github.zerowise.neptune.consumer;

import java.util.function.Consumer;

import com.github.zerowise.neptune.Service;
import com.github.zerowise.neptune.kernel.CodecFactory;
import com.github.zerowise.neptune.kernel.ResponseMessage;
import com.github.zerowise.neptune.kernel.Session;

public interface RpcConsumer extends Service {

	default void start() throws Throwable {
	};

	Session start(Consumer<ResponseMessage> consumer, String host, int inetPort);

	Session start(CodecFactory codecFactory, Consumer<ResponseMessage> consumer, String host, int inetPort);

}
