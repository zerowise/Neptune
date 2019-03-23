package com.github.zerowise.neptune;

import java.util.Objects;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Configs {

	private static Config config;
	static {
		config = ConfigFactory.load("neptune.conf");
	}

	public static String getString(String path) {
		Objects.requireNonNull(path);
		return Objects.requireNonNull(config.getString(path));
	}

	public static boolean hasPath(String path) {
		Objects.requireNonNull(path);
		return config.hasPath(path);
	}

	public static int getInt(String path, int defaultVal) {
		Objects.requireNonNull(path);
		return hasPath(path) ? config.getInt(path) : defaultVal;
	}
}
