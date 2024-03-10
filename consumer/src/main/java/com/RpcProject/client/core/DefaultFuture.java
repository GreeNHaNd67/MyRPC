package com.RpcProject.client.core;

import com.RpcProject.client.param.ClientRequest;
import com.RpcProject.client.param.Response;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture {
    public final static ConcurrentHashMap<Long,DefaultFuture> allDefaultFuture = new ConcurrentHashMap<>();
    final Lock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();
    private Response response;
    private long timeout=2*60* 1000L;
    private long startTime = System.currentTimeMillis();

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public DefaultFuture(ClientRequest request){
         allDefaultFuture.put(request.getID(),this);
    }
    //主线程获取数据，首先要等待结果
    public Response get(){
        lock.lock();
        try{
            while(!done()){
                condition.await();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return this.response;
    }

    public Response get(long time){
        lock.lock();
        try{
            while(!done()){
                condition.await(time, TimeUnit.MILLISECONDS);
                if((System.currentTimeMillis()-startTime)>time){
                    //System.out.println("Request Timeout!");
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return this.response;
    }

    public static void receive(Response response){
        DefaultFuture defaultFuture = allDefaultFuture.get(response.getId());
        if(defaultFuture !=null){
            Lock lock = defaultFuture.lock;
            lock.lock();
            try {
                defaultFuture.setResponse(response);
                defaultFuture.condition.signal();
                allDefaultFuture.remove(defaultFuture);
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

    }
    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    private boolean done() {
        if(this.response!=null){
            return true;
        }
        return false;
    }

    static class FutureThread extends Thread{
        @Override
        public void run() {
            Set<Long> ids = allDefaultFuture.keySet();
            for(Long id : ids){
                DefaultFuture defaultFuture = allDefaultFuture.get(id);
                if(defaultFuture==null) {
                    allDefaultFuture.remove(defaultFuture);
                }
                else {
                    //Connection Timeout
                    if(defaultFuture.getTimeout()<(System.currentTimeMillis()-defaultFuture.getStartTime())){
                        Response resp = new Response();
                        resp.setId(id);
                        resp.setCode("44444");
                        resp.setMsg("Connection Timeout");
                        receive(resp);
                    }
                }
            }
        }
    }

    static {
        FutureThread futureThread = new FutureThread();
        futureThread.setDaemon(true);
        futureThread.start();
    }

}
