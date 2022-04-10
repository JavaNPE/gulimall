package com.atguigu.gulimall.search.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Dali
 * @Date 2022/4/10 15:53
 * @Version 1.0
 * @Description
 */
public class ThreadTest {
	public static ExecutorService service = Executors.newFixedThreadPool(10);

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		System.out.println("main....start....");
		/**
		 * 1)、继承Thread
		 * 2)、实现Runnable接口
		 * 3)、实现Callable接口+ FutureTask (可以拿到返回结果，可以处理异常)
		 * 4)、线程池： 给线程池直接提交任务
		 * 	区别;
		 * 		1、2:不能得到返回值。3:可以获取返回值
		 * 		1、2、3:都不能控制资源
		 * 		4:可以控制资源
		 */
		// -------------方式1、继承Thread 启动线程 begin--------------
/*		Thread01 thread01 = new Thread01();
		thread01.start();*/
		// -------------方式1、继承Thread 启动线程 end--------------

		// -------------方式2、继实现Runnable接口 启动线程 begin--------------
/*		Runnable01 runnable01 = new Runnable01();
		new Thread(runnable01).start();*/
		// -------------方式2、实现Runnable接口 启动线程 end--------------

		// -------------方式3、实现Callable接口+ FutureTask 启动线程 begin--------------
/*		FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
		new Thread(futureTask).start();
		// 阻塞等待线程执行完成，获取返回结果
		Integer integer = futureTask.get();*/
		// -------------方式3、实现Callable接口+ FutureTask 启动线程 begin--------------

		// 我们以后再业务代码里面，以上三种启动线程的方式都不用。[将所有的多线程异步任务都交给线程池执行]
		// new Thread(() -> System.out.println("hello")).start();

		// 当前系统中线程池只有一两个，每个异步任务，提交给线程池让他自己去执行就行。
		service.execute(new Runnable01());

		System.out.println("main....end...."/* + integer*/);
	}

	/**
	 * 1、继承Thread, 需要实现run() 方法
	 */
	public static class Thread01 extends Thread {
		@Override
		public void run() {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 2;
			System.out.println("运行结果：" + i);
		}
	}

	/**
	 * 2、实现Runnable接口
	 */
	public static class Runnable01 implements Runnable {

		@Override
		public void run() {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 2;
			System.out.println("运行结果：" + i);
		}
	}

	/**
	 * 3、实现Callable接口+ FutureTask (可以拿到返回结果，可以处理异常)，需要实现call()方法
	 */
	public static class Callable01 implements Callable<Integer> {

		@Override
		public Integer call() throws Exception {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 2;
			System.out.println("运行结果：" + i);
			return i;
		}
	}
}
