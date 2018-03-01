public class StaticVoltHandler implements IStaticVoltHandler {
        int storedPower;
        int maxStoredPower;
        int maxRecieve;
        int maxDrain;
        
        boolean canRecieve;
        boolean canDrain;
        
        public StaticVoltHandler(int capacity, int maxRecieve, int maxDrain) {
            this.maxStoredPower = capacity;
            this.maxRecieve = maxRecieve;
            this.maxDrain =maxDrain;
        }
        
        @Override
        public int getStoredPower() {
            return storedPower;
        }
        @Override 
        public int getPowerCapacity() {
            return capacity;
        }
        
        
        
                
      



}