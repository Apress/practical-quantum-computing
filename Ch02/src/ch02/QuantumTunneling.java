package ch02;

/**
 * Tiny implementation of the Quantum Tunneling probability described by Thomas
 * Engel in
 * 
 * Engel, Thomas. Quantum Chemistry and Spectroscopy.Upper Saddle River, NJ.
 * Pearson, 2006. Print.
 * 
 * 
 */
public class QuantumTunneling {

    /** Planck's constant */
    static final double K_PLANK         = 6.626e-34;

    /** Mass of the electron (kg) */
    static final double K_ELECTRON_MASS = 9.1e-31;

    /** Electron volt */
    static final double K_EV            = 1.6e-19;

    /**
     * Engel's Quantum Tunneling Probability
     * 
     * @param size
     *            Size of the barrier in meters.
     * @param E
     *            Kinetic energy in electron volts (eV).
     * @param V
     *            Potential energy in eV.
     * @return Quantum Tunneling Probability
     */
    static double EngelProbability(double size, double E, double V) {
        if (E > V) {
            throw new IllegalArgumentException("Potential energy (V) must be > Kinectic Energy (E)");
        }
        double delta = V - E;
        double p1 = ((-4 * size * Math.PI) / K_PLANK);
        double p2 = Math.sqrt(2 * K_ELECTRON_MASS * delta * K_EV);
        return Math.exp(p1 * p2);
    }

    /** A simple test for for current semiconductor processes */
    public static void main(String[] args) {
        System.out.println("Quatum tunneling probabilities for current semiconductor processes.");
        try {
            // Barrier sizes for current semiconductor processes (m)
            final double[] SIZES = { 130e-9, 32e-9, 14e-9, 7e-9, 5e-9, 500e-12 };

            // Dates for display puposes
            final String[] DATES = { "2001", "2010", "2014", "2019", "2021", "Beyond" };

            final double E = 4.5; // Kinetic energy of the electorn (eV)
            final double V = 5.0; // Potential energy (eV)

            // Display them...
            for (int i = 0; i < DATES.length; i++) {
                double p = EngelProbability(SIZES[i], E, V);

                System.out.println(String.format("%s\t%2.2e\t%2.3e", DATES[i], SIZES[i], p));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
