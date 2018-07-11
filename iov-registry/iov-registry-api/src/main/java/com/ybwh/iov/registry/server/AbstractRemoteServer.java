package com.ybwh.iov.registry.server;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRemoteServer {

	private static final Logger logger = LoggerFactory.getLogger(AbstractRemoteServer.class);

	protected int port;

	/** server init status */
	private AtomicBoolean inited = new AtomicBoolean(false);

	/** server start status */
	private AtomicBoolean started = new AtomicBoolean(false);

	/**
	 * @param port
	 */
	public AbstractRemoteServer(int port) {
		this.port = port;
	}

	/**
	 * Initialize.
	 */
	public void init() {
		if (inited.compareAndSet(false, true)) {
			logger.warn("Initialize the server.");
			this.doInit();
		} else {
			logger.warn("Server has been inited already.");
		}
	}

	/**
	 * Start the server.
	 */
	public boolean start() {
		this.init();
		if (started.compareAndSet(false, true)) {
			try {
				logger.warn("Server started on port: " + port);
				return this.doStart();
			} catch (Throwable t) {
				started.set(false);
				this.stop();
				logger.error("ERROR: Failed to start the Server!", t);
				return false;
			}
		} else {
			String errMsg = "ERROR: The server has already started!";
			logger.error(errMsg);
			throw new IllegalStateException(errMsg);
		}
	}

	/**
	 * Start the server with ip and port.
	 */
	public boolean start(String ip) {
		this.init();
		if (started.compareAndSet(false, true)) {
			try {
				logger.warn("Server started on " + ip + ":" + port);
				return this.doStart(ip);
			} catch (Throwable t) {
				started.set(false);
				this.stop();
				logger.error("ERROR: Failed to start the Server!", t);
				return false;
			}
		} else {
			String errMsg = "ERROR: The server has already started!";
			logger.error(errMsg);
			throw new IllegalStateException(errMsg);
		}
	}

	/**
	 * Stop the server.
	 * <p>
	 * Notice:<br>
	 * <li>Remoting server can not be used any more after shutdown.
	 * <li>If you need, you should destroy it, and instantiate another one.
	 */
	public void stop() {
		if (inited.get() || started.get()) {
			inited.compareAndSet(true, false);
			started.compareAndSet(true, false);
			this.doStop();
		} else {
			throw new IllegalStateException("ERROR: The server has already stopped!");
		}
	}

	/**
	 * Get the port of the server.
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Inject initialize logic here.
	 */
	protected abstract void doInit();

	/**
	 * Inject start logic here.
	 * 
	 * @throws InterruptedException
	 */
	protected abstract boolean doStart() throws InterruptedException;

	/**
	 * Inject start logic here.
	 * 
	 * @param ip
	 * @throws InterruptedException
	 */
	protected abstract boolean doStart(String ip) throws InterruptedException;

	/**
	 * Inject stop logic here.
	 */
	protected abstract void doStop();

}
