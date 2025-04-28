public class Manager {
    private volatile boolean cracked = false; //Volatile variable to indicate other threads if a thread has found the passcode

    /**
     * Gets the current state of whether the password has been cracked.
     * 
     * @return true if the password is cracked, false otherwise.
     */
    public boolean getCracked(){
        return this.cracked;
    }

    public void setCracked(boolean cracked){
        this.cracked = cracked;
    }
}
