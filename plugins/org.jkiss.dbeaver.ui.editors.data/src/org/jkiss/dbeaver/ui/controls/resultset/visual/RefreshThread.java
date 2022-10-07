package org.jkiss.dbeaver.ui.controls.resultset.visual;

public class RefreshThread extends Thread {
    public static interface Refreshable {
        public void refreshWork();
    }

    private int interval;

    public RefreshThread(Refreshable view, int interval) {
        this.view = view;
        this.setDaemon(true);
        this.interval = interval;
        this.setName(view.toString());
    }

    final private Refreshable view;

    public void setThreadName(String name){
        this.setName(name);
    }
    
    public void run() {

        while (brun) {
            view.refreshWork();
            try{
                Thread.sleep(interval);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private boolean brun = true;

    public void shutdown() {
        brun = false;
    }
}