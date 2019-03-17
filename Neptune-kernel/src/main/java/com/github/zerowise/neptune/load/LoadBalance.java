package com.github.zerowise.neptune.load;

import java.util.List;

public interface LoadBalance<T> {

	T select();
	
	void update(List<T> list);
}
