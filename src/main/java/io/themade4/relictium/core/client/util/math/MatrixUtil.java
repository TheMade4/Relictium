package io.themade4.relictium.core.client.util.math;

import io.themade4.relictium.core.client.util.Norm3b;
import net.minecraft.util.EnumFacing;
import repack.joml.Matrix3f;
import repack.joml.Matrix4f;

public class MatrixUtil {
    public static int computeNormal(Matrix3f normalMatrix, EnumFacing facing) {
        return ((Matrix3fExtended) normalMatrix).computeNormal(facing);
    }

    public static Matrix4fExtended getExtendedMatrix(Matrix4f matrix) {
        return (Matrix4fExtended) (Object) matrix;
    }

    public static Matrix3fExtended getExtendedMatrix(Matrix3f matrix) {
        return (Matrix3fExtended) matrix;
    }

    public static int transformPackedNormal(int norm, Matrix3f matrix) {
        Matrix3fExtended mat = MatrixUtil.getExtendedMatrix(matrix);

        float normX1 = Norm3b.unpackX(norm);
        float normY1 = Norm3b.unpackY(norm);
        float normZ1 = Norm3b.unpackZ(norm);

        float normX2 = mat.transformVecX(normX1, normY1, normZ1);
        float normY2 = mat.transformVecY(normX1, normY1, normZ1);
        float normZ2 = mat.transformVecZ(normX1, normY1, normZ1);

        return Norm3b.pack(normX2, normY2, normZ2);
    }
}
