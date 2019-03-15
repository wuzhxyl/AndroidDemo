package com.ilifesmart.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private static final int CORE_POOL_SZIE = Math.max(2, Math.min(CPU_COUNT-1, 4));
	private static final int MAX_POOL_SIZE = Integer.MAX_VALUE;
	private static final int KEEP_ALIVE_SECONDS = 30;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private AtomicInteger mCount = new AtomicInteger(1);
		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "ThreadPool #" + mCount.getAndIncrement());
		}
	};

	private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingDeque<>(128);

	public static final Executor THREAD_POOL_EXECUTOR;

	static {
		THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SZIE, MAX_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
	}

}
