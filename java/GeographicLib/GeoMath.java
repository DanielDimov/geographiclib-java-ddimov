/**
 * @file GeoMath.java
 * @brief Implementation of the GeographicLib.GeoMath class
 *
 * Copyright (c) Charles Karney (2013) <charles@karney.com> and licensed
 * under the MIT/X11 License.  For more information, see
 * http://geographiclib.sourceforge.net/
 **********************************************************************/
package GeographicLib;
  /**
   * @brief Mathematical functions needed by GeographicLib
   *
   * Define mathematical functions and constants so that any version of Java
   * can be used.
   **********************************************************************/
public class GeoMath {
  /**
   * The number of binary digits in the fraction of a double precision
   * number (equivalent to C++'s numeric_limits<double>::digits).
   **********************************************************************/
  public static final int digits = 53;
  /**
   * Equivalent to C++'s numeric_limits<double>::epsilon().  Math.ulp(1.0) can
   * be used in Java version 1.5.
   **********************************************************************/
  public static final double epsilon = Math.pow(0.5, digits - 1);
  /**
   * Equivalent to C++'s numeric_limits<double>::min().  Double.MIN_NORMAL can
   * be used in Java version 1.6.
   **********************************************************************/
  public static final double min = Math.pow(0.5, 1022);
  /**
   * The number of radians in a degree.
   **********************************************************************/
  public static final double degree = Math.PI / 180;

  /**
   * Square a number.
   *
   * @param x
   * @return <i>x</i><sup>2</sup>.
   **********************************************************************/
  public static double sq(double x) { return x * x; }

  /**
   * The hypotenuse function avoiding underflow and overflow.  Math.hypot
   * appeared in Java version 1.5.
   *
   * @param x
   * @param y
   * @return sqrt(<i>x</i><sup>2</sup> + <i>y</i><sup>2</sup>).
   **********************************************************************/
  public static double hypot(double x, double y) {
    x = Math.abs(x); y = Math.abs(y);
    double a = Math.max(x, y), b = Math.min(x, y) / (a != 0 ? a : 1);
    return a * Math.sqrt(1 + b * b);
    // For an alternative method see
    // C. Moler and D. Morrision (1983) http://dx.doi.org/10.1147/rd.276.0577
    // and A. A. Dubrulle (1983) http://dx.doi.org/10.1147/rd.276.0582
  }

  /**
   * log(1 + \e x) accurate near \e x = 0.  Math.log1p appeared in Java version
   * 1.5.
   *
   * This is taken from D. Goldberg,
   * <a href="http://dx.doi.org/10.1145/103162.103163">What every computer
   * scientist should know about floating-point arithmetic</a> (1991),
   * Theorem 4.  See also, N. J. Higham, Accuracy and Stability of Numerical
   * Algorithms, 2nd Edition (SIAM, 2002), Answer to Problem 1.5, p 528.
   *
   * @param x
   * @return log(1 + \e x).
   **********************************************************************/
  public static double log1p(double x) {
    double
      y = 1 + x,
      z = y - 1;
    // Here's the explanation for this magic: y = 1 + z, exactly, and z
    // approx x, thus log(y)/z (which is nearly constant near z = 0) returns
    // a good approximation to the true log(1 + x)/x.  The multiplication x *
    // (log(y)/z) introduces little additional error.
    return z == 0 ? x : x * Math.log(y) / z;
  }

  /**
   * The inverse hyperbolic tangent function.  This is defined in terms of
   * GeoMath.log1p(\e x) in order to maintain accuracy near \e x = 0.  In
   * addition, the odd parity of the function is enforced.
   *
   * @param x
   * @return atanh(\e x).
   **********************************************************************/
  public static double atanh(double x)  {
    double y = Math.abs(x);     // Enforce odd parity
    y = Math.log1p(2 * y/(1 - y))/2;
    return x < 0 ? -y : y;
  }

  /**
   * The cube root function.  Math.cbrt appeared in Java version 1.5.
   *
   * @param x
   * @return the real cube root of \e x.
   **********************************************************************/
  public static double cbrt(double x) {
    double y = Math.pow(Math.abs(x), 1/3.0); // Return the real cube root
    return x < 0 ? -y : y;
  }

  /**
   * The error-free sum of two numbers.
   *
   * @param u
   * @param v
   * @return Pair(\e s, \e t) with \e s = round(\e u + \e v) and \e t = \e u +
   *   \e v - \e a
   *
   * See D. E. Knuth, TAOCP, Vol 2, 4.2.2, Theorem B.
   **********************************************************************/
  public static Pair sum(double u, double v) {
    double s = u + v;
    double up = s - v;
    double vpp = s - up;
    up -= u;
    vpp -= v;
    double t = -(up + vpp);
    // u + v =       s      + t
    //       = round(u + v) + t
    return new Pair(s, t);
  }

  /**
   * Normalize an angle (restricted input range).
   *
   * @param x the angle in degrees.
   * @return the angle reduced to the range [&minus;180&deg;, 180&deg;).
   *
   * \e x must lie in [&minus;540&deg;, 540&deg;).
   **********************************************************************/
  public static double AngNormalize(double x)
  { return x >= 180 ? x - 360 : (x < -180 ? x + 360 : x); }

  /**
   * Normalize an arbitrary angle.
   *
   * @param x the angle in degrees.
   * @return the angle reduced to the range [&minus;180&deg;, 180&deg;).
   *
   * The range of \e x is unrestricted.
   **********************************************************************/
  public static double AngNormalize2(double x)
  { return AngNormalize(x % 360.0); }

  /**
   * Difference of two angles reduced to [&minus;180&deg;, 180&deg;]
   *
   * @param x the first angle in degrees.
   * @param y the second angle in degrees.
   * @return \e y &minus; \e x, reduced to the range [&minus;180&deg;,
   *   180&deg;].
   *
   * \e x and \e y must both lie in [&minus;180&deg;, 180&deg;].  The result
   * is equivalent to computing the difference exactly, reducing it to
   * (&minus;180&deg;, 180&deg;] and rounding the result.  Note that this
   * prescription allows &minus;180&deg; to be returned (e.g., if \e x is
   * tiny and negative and \e y = 180&deg;).
   **********************************************************************/
  public static double AngDiff(double x, double y) {
    double d, t;
    { Pair r = sum(-x, y); d = r.first; t = r.second; }
    if ((d - 180.0) + t > 0.0) // y - x > 180
      d -= 360.0;            // exact
    else if ((d + 180.0) + t <= 0.0) // y - x <= -180
      d += 360.0;            // exact
    return d + t;
  }
    /**
     * Test for finiteness.
     *
     * @param x
     * @return true if number is finite, false if NaN or infinite.
     **********************************************************************/
    public static boolean isfinite(double x) {
      return Math.abs(x) <= Double.MAX_VALUE;
    }
}
