package fi.tamk.ratboyz.tamperecitybike.viewmodels;


import java.util.ArrayList;
import java.util.List;

import fi.tamk.ratboyz.tamperecitybike.Config;
import fi.tamk.ratboyz.tamperecitybike.interfaces.APIResponse;
import fi.tamk.ratboyz.tamperecitybike.models.Note;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class NoteViewModel {

    private Note note;
    private RestAPIService service;



    protected NoteViewModel(Note note) {
        this.note = note;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(RestAPIService.class);
    }

    /////////////////////
    //    API CALLS    //
    /////////////////////


    public static void getAll(final APIResponse<List<NoteViewModel>> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestAPIService service = retrofit.create(RestAPIService.class);
        Call<List<Note>> call = service.get();
        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                if (response.isSuccessful()) {
                    ArrayList<NoteViewModel> notes = new ArrayList<>(response.body().size());
                    for (Note note : response.body()) {
                        notes.add(new NoteViewModel(note));
                    }
                    callback.onSuccess(notes);
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    public static void createNew(Note.Proto protoNote, final APIResponse<NoteViewModel> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestAPIService service = retrofit.create(RestAPIService.class);
        Call<Note> call = service.post(protoNote);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(new NoteViewModel(response.body()));
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    public static void getById(String id, final APIResponse<NoteViewModel> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestAPIService service = retrofit.create(RestAPIService.class);
        Call<Note> call = service.get(id);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(new NoteViewModel(response.body()));
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    public void commitChanges(final APIResponse<NoteViewModel> callback) {
        Call<Note> call = this.service.put(this.note.getId(), this.note);
        final NoteViewModel noteViewModel = this;
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.isSuccessful()) {
                    noteViewModel.note = response.body();
                    callback.onSuccess(noteViewModel);
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    public static void delete(final NoteViewModel noteViewModel, final APIResponse<NoteViewModel> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RestAPIService service = retrofit.create(RestAPIService.class);
        Call<Note> call = service.delete(noteViewModel.note.getId());
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.isSuccessful()) {
                    noteViewModel.note = response.body();
                    callback.onSuccess(noteViewModel);
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    private interface RestAPIService {
        @GET("notes/")
        Call<List<Note>> get();

        @POST("notes/")
        Call<Note> post(@Body Note.Proto protoNote);

        @GET("notes/{id}")
        Call<Note> get(@Path("id") String id);

        @PUT("notes/{id]")
        Call<Note> put(@Path("id") String id, @Body Note note);

        @DELETE("notes/{id}")
        Call<Note> delete(@Path("id") String id);
    }
}
