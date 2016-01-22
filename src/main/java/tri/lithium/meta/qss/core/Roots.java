package tri.lithium.meta.qss.core;

/**
 * Utility class for calculating the least positive real roots of polynomials.
 */
public final class Roots {

    private static final double TOLERANCE = 1.0e-6;
    private static final double ZERO_TOLERANCE = 1.0e-9;

    private static boolean nearZero(final double value) {
        return Math.abs(value) < ZERO_TOLERANCE;
    }

    /**
     * Find the least positive cubic root
     * @param A Ax^4
     * @param B Bx^3
     * @param C Cx^2
     * @param D Dx
     * @param E E
     * @return the least positive root or infinity if divergent (all roots < 0)
     */
    public static double findQuarticRoot(double A, double B, double C, double D, double E) {
        double root = Double.POSITIVE_INFINITY;

        if (!nearZero(A) && Math.abs(A / B) >= TOLERANCE) {
            final double a = B / A;
            final double b = C / A;
            final double c = D / A;
            final double d = E / A;

            double y = findCubicRoot(1.0, b, a * c - 4 * d, 4 * b * d - b * b * d - c * c);

            double sq1 = Math.sqrt(a * a - 4 * d + 4 * y);
            double sq2 = Math.sqrt(y * y - 4 * d);

            double p1 = a + sq1;
            double p2 = a - sq1;

            double q1 = y - sq2;
            double q2 = y + sq2;

            double x1 = ( Math.sqrt(p1 * p1 - 8 * q1) - p1 ) / 4d;
            double x2 = ( Math.sqrt(p2 * p2 - 8 * q1) - p2 ) / 4d;
            double x3 = ( Math.sqrt(p1 * p1 - 8 * q2) - p1 ) / 4d;
            double x4 = ( Math.sqrt(p2 * p2 - 8 * q2) - p2 ) / 4d;

            if (x1 >= 0) root = x1;
            if (x2 >= 0 && x2 < root) root = x2;
            if (x3 >= 0 && x3 < root) root = x3;
            if (x4 >= 0 && x4 < root) root = x4;

        } else
            root = findCubicRoot(B, C, D, E);

        return root;
    }

    /**
     * Find the least positive cubic root
     * @param A Ax^3
     * @param B Bx^2
     * @param C Cx
     * @param D D
     * @return the least positive root or infinity if divergent (all roots < 0)
     */
    public static double findCubicRoot(final double A, final double B, final double C, final double D) {
        double root = Double.POSITIVE_INFINITY;

        if (A != 0 && Math.abs( A / B ) >= TOLERANCE) {
            final double a = B / A;
            final double b = C / A;
            final double c = D / A;

            final double R = (2 * a * a * a - 9 * a * b + 27 * c) / 54d;
            final double Q = (a * a - 3 * b) / 9d;

            final double QCube = Q * Q * Q;
            final double M = R * R - QCube;

            if (M > 0) {

                // one real root
                double Msq = Math.sqrt(M);
                double x1  = Math.cbrt(-R + Msq) + Math.cbrt(-R - Msq) - a / 3d;

                if (x1 > 0) root = x1;

            } else if (M < 0) {

                // three real roots
                double theta = Math.acos(R / Math.sqrt(QCube));

                double Qsq = 2 * Math.sqrt(Q);

                double x1 = -(Qsq * Math.cos(theta / 3)) - a / 3;
                double x2 = -(Qsq * Math.cos((theta - 2 * Math.PI) / 3)) - a / 3d;
                double x3 = -(Qsq * Math.cos((theta + 2 * Math.PI) / 3)) - a / 3d;

                if (x1 >= 0) root = x1;
                if (x2 >= 0 && x2 < root) root = x2;
                if (x3 >= 0 && x3 < root) root = x3;

            } else {
                // three real, min two equal roots,
                double D0 = B * B - 3 * A * C;
                double x1;

                if (nearZero(D0)) {
                    // two equal roots, one simple root
                    x1 = -B / (3 * A);
                } else {
                    // all roots are equal
                    x1 = (9 * A * D - B * C) / 2d * D0;
                }

                if (x1 > 0) root = x1;
            }
        } else
            root = findQuadraticRoot(B, C, D);

        return root;
    }

    /**
     * Find the least positive quadratic root
     * @param A Ax^2
     * @param B Bx
     * @param C C
     * @return the least positive root or infinity if divergent (all roots < 0)
     */
    public static double findQuadraticRoot(final double A, final double B, final double C) {
        double root = Double.POSITIVE_INFINITY;

        if (!nearZero(A) && Math.abs( A / B ) >= TOLERANCE) {
            double M  = (B * B - 4 * A * C);
            double re = -B / (2 * A);

            if (M >= 0) {
                double Msq = Math.sqrt(M);
                double x1 =   Msq / (2 * A) + re;
                double x2 = - Msq / (2 * A) + re;

                if (x1 >= 0) root = x1;
                if (x2 >= 0 && x2 < root) root = x2;
            }
        } else
            root = findSimpleRoot(B, C);

        return root;
    }

    /**
     * Find the least positive linear root
     * @param A Ax
     * @param B B
     * @return the least positive root or infinity if divergent (roots < 0)
     */
    public static double findSimpleRoot(double A, double B) {
        double root = Double.POSITIVE_INFINITY;

        if (!nearZero(A)) {
          double d = -B / A;
          if (d >= 0) root = d;
        }

        return root;
    }

}
