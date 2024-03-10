package com.RpcProject.netty.client;

import com.RpcProject.netty.util.Response;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture {
    public final static ConcurrentHashMap<Long,DefaultFuture> allDefaultFuture = new ConcurrentHashMap<>();

    final Lock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();
    private Response response;
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
}
