package com.github.zerowise.neptune.kernel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class CodecFactory {

	private final Class<?> clazz;
	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

	public CodecFactory(Class<?> clazz) {
		super();
		this.clazz = clazz;
	}

	public void build(Channel channel) {
		channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4) {

			@Override
			protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
				ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
				byte[] bytes = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(bytes);
				return deserializer(bytes, clazz);
			}
		}, new MessageToByteEncoder<Object>() {

			@Override
			protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
				byte[] bytes = serializer(msg);
				out.writeInt(bytes.length).writeBytes(bytes);
			}
		});
	}

	private static <T> Schema<T> getSchema(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
		if (schema == null) {
			schema = RuntimeSchema.getSchema(clazz);
			if (schema != null) {
				cachedSchema.put(clazz, schema);
			}
		}
		return schema;
	}

	/**
	 * 序列化
	 *
	 * @param obj
	 * @return
	 */
	public static <T> byte[] serializer(T obj) {
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) obj.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try {
			Schema<T> schema = getSchema(clazz);
			return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			buffer.clear();
		}
	}

	/**
	 * 反序列化
	 *
	 * @param data
	 * @param clazz
	 * @return
	 */
	public static <T> T deserializer(byte[] data, Class<T> clazz) {
		try {
			T obj = clazz.newInstance();
			Schema<T> schema = getSchema(clazz);
			ProtostuffIOUtil.mergeFrom(data, obj, schema);
			return obj;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}