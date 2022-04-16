package com.atguigu.gulimall.search.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
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
	// 创建线程池，供后面使用
	public static ExecutorService executor = Executors.newFixedThreadPool(10);

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		System.out.println("main....start....");
/*		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 2;
			System.out.println("运行结果：" + i);
		}, executor);*/

		/**
		 *  方法完成后的感知
		 */
/*		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 0;
			System.out.println("运行结果：" + i);
			return i;
		}, executor).whenComplete((res, excption) -> {
			// 虽然能得到异常信息，但是没法修改返回数据。
			System.out.println("异步任务成功完成了...结果是：" + res + ";异常是：" + excption);
		}).exceptionally(throwable -> {
			// 可以感知异常，同时返回默认值
			return 10;
		});
		Integer integer = future.get();*/
		/**
		 * 方法异步执行完成后的处理
		 */
/*		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 5;
			System.out.println("运行结果：" + i);
			return i;
		}, executor).handle((res, thr) -> {
			// res: 返回的结果
			if (res != null) {
				return res * 2;
			}
			// thr: 异常
			if (thr != null) {
				return 0;
			}
			return 0;
		});
		// R apply(T t, U u)
		Integer integer = future.get();
		System.out.println("main....end...." + integer);
		*/

		/**
		 * 	线程串行化：
		 * 		1、thenRun：不能获取到上一步的执行结果，没有返回值
		 * 		.thenRunAsync(() -> {
		 * 			System.out.println("任务（线程）2启动了");
		 *                }, executor);
		 *      2、thenAcceptAsync：能接收上一步的结果，但是无返回值
		 *      .thenAcceptAsync((res) -> {
		 * 			// void accept(T t);
		 * 			System.out.println("任务（线程）2启动了" + res);
		 *                }, executor);
		 *      3、thenAcceptAsync：能接收上一步结果，有返回值
		 *      .thenApplyAsync((res) -> {
		 * 			// R apply(T t);
		 * 			System.out.println("任务（线程）2启动了" + res);
		 * 			return "Hello " + res;
		 *                }, executor);
		 * 		System.out.println("main....end...." + future.get());
		 */
		// 1、thenRun：不能获取到上一步的执行结果，没有返回值
		/*		CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 5;
			System.out.println("运行结果：" + i);
			return i;
		}, executor).thenRunAsync(() -> {
			System.out.println("任务（线程）2启动了");
		}, executor);
		System.out.println("main....end....");*/

		// 2、thenAcceptAsync：能接收上一步的结果，但是无返回值
		/*CompletableFuture.supplyAsync(() -> {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 5;
			System.out.println("运行结果：" + i);
			return i;
		}, executor).thenAcceptAsync((res) -> {
			// void accept(T t);
			System.out.println("任务（线程）2启动了" + res);
		}, executor);
		System.out.println("main....end....");*/

		// 3、thenAcceptAsync：能接收上一步结果，有返回值
		/*CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 5;
			System.out.println("运行结果：" + i);
			return i;
		}, executor).thenApplyAsync((res) -> {
			// R apply(T t);
			System.out.println("任务（线程）2启动了" + res);
			return "Hello " + res;
		}, executor);
		System.out.println("main....end...." + future.get());*/

		/**
		 *  两个异步线程都完成
		 */
		CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
			System.out.println("任务1线程：" + Thread.currentThread().getId());
			int i = 10 / 5;
			System.out.println("任务1结束：" + i);
			return i;
		}, executor);

		CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
			System.out.println("任务2线程：" + Thread.currentThread().getId());
			System.out.println("任务2结束：");
			return "Hello";
		}, executor);

		// 任务1执行完之后 开启 任务2 等 1和2都执行完之后 在执行任务3
		// runAfterBothAsync：无法感知前两个线程的结果
		/*future01.runAfterBothAsync(future02, () -> {
			System.out.println("任务3开始...");
		}, executor);
		System.out.println("main....end....");*/

		// thenAcceptBothAsync： 可以感知前两个线程的结果值
		/*future01.thenAcceptBothAsync(future02, (f1, f2) -> {
			// void accept(T t, U u);
			System.out.println("任务3开始...任务1的结果：" + f1 + "; 任务2的结果：" + f2);
		}, executor);
		System.out.println("main....end....");*/

		// thenCombineAsync合并多个任务，既能结束前面线程的返回值，又能其返回值进行操作，return输出自己（新线程）的值。
		CompletableFuture<String> future = future01.thenCombineAsync(future02, (f1, f2) -> {
			System.out.println("任务3开始...");
			return "线程1的值：" + f1 + "；线程2的值：" + f2 + "；  另外拼接-> Haha";
		}, executor);
		System.out.println("main....end...." + future.get());
	}

	public void thread(String[] args) throws ExecutionException, InterruptedException {
		System.out.println("main....start....");
		/**
		 * 1)、继承Thread
		 * 2)、实现Runnable接口
		 * 3)、实现Callable接口+ FutureTask (可以拿到返回结果，可以处理异常)
		 * 4)、线程池【指代ExecutorService下面的东西】： 给线程池直接提交任务 service.execute(new Runnable01());
		 * 		1、线程池的创建：
		 * 			1.1 通过Executors创建
		 * 			1.2 通过 new ThreadPoolExecutor创建
		 * 	区别;
		 * 		1、2:不能得到返回值。3:可以获取返回值
		 * 		1、2、3:都不能控制资源
		 * 		4:可以控制资源
		 * 	线程池的运行流程:
		 * 		1、线程池创建，准备好core数量的核心线程，准备接受任务
		 * 		2、新的任务进来，用core准备好的空闲线程执行。中
		 * 				(1)、core满了，就将再进来的任务放入阻塞队列中。空闲的core就会自己去阻塞队列获取任务执行
		 * 				(2)、阻塞队列满了，就直接开新线程执行，最大只能开到max指定的数量
		 * 				(3)、max都执行好了。Max-core数量空闲的线程会在keepAliveTime指定的时间后自动销毁。最终保持到core大小
		 * 				(4)、 如果线程数开到了max的数量，还有新任务进来，就会使用reject 指定的拒绝策略进行处理
		 * 		3、所有的线程创建都是由指定的factory创建的。
		 *
		 * Future:可以获取到异步结果
		 *
		 * 线程池面试题：一个线程池 core7; max20，queue: 50, 100 并发进来怎么分配的？
		 *
		 * 先有7个能直接得到执行，接下来50个进入队列排队，在多开13个继续执行。现在70（20 +  50）个被安排上了。剩下30个默认拒绝策略。
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
		/*service.execute(new Runnable01());*/
//		new ThreadPoolExecutor();

// ---------------------------------------常见的4种线程池------------------------------------------
//		Executors.newCachedThreadPool(); // 核心是0，所有都可回收。
//		Executors.newFixedThreadPool();	// 固定大小，core=max；都不可回收
//		Executors.newScheduledThreadPool();	// 定时任务的线程池
//		Executors.newSingleThreadExecutor();	//单线程的线程池，后台从队列里面获取任务，挨个执行
// ---------------------------------------常见的4种线程池------------------------------------------

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
