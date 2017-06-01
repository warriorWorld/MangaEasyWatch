package com.warrior.hangsu.administrator.mangaeasywatch.utils;

public interface HttpCallbackListener {
	void onFinish(String response);

	void onError(Exception e);
}
