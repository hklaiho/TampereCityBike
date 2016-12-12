package fi.tamk.ratboyz.tamperecitybike.interfaces;

public interface APIResponse<T> {
    void onSuccess(T response);
    void onFailure();
}
