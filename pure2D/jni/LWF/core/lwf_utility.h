#ifndef LWF_UTILITY_H
#define LWF_UTILITY_H

namespace LWF {

class LWF;
class Matrix;
class Movie;
class ColorTransform;

class Utility
{
public:
	static void CalcMatrixToPoint(
		float &dx, float &dy, float sx, float sy, const Matrix *m);
	static bool GetMatrixDeterminant(const Matrix *matrix);
	static void SyncMatrix(Movie *movie);
	static float GetX(const Movie *movie);
	static float GetY(const Movie *movie);
	static float GetScaleX(const Movie *movie);
	static float GetScaleY(const Movie *movie);
	static float GetRotation(const Movie *movie);
	static void SyncColorTransform(Movie *movie);
	static float GetAlpha(const Movie *movie);
	static float GetRed(const Movie *movie);
	static float GetGreen(const Movie *movie);
	static float GetBlue(const Movie *movie);
	static Matrix *CalcMatrix(
		LWF *lwf, Matrix *dst, const Matrix *src0, int src1Id);
	static Matrix *CalcMatrix(
		Matrix *dst, const Matrix *src0, const Matrix *src1);
	static Matrix *CopyMatrix(Matrix *dst, const Matrix *src);
	static void InvertMatrix(Matrix *dst, const Matrix *src);
	static ColorTransform *CalcColorTransform(LWF *lwf,
		ColorTransform *dst, const ColorTransform *src0, int src1Id);
	static ColorTransform *CalcColorTransform(ColorTransform *dst,
		const ColorTransform *src0, const ColorTransform *src1);
	static ColorTransform *CopyColorTransform(
		ColorTransform *dst, const ColorTransform *src);
	static void CalcColor(Color *dst, const Color *c, const ColorTransform *t);
	static vector<string> Split(const string &str, char d);
};

}	// namespace LWF

#endif
