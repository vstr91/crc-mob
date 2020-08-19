package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.util.List;

import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.DataHoraUtils;

public class InfoWindowParadaViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    ParadaBairro parada;
    public Bitmap foto;
    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(ParadaBairro parada) {
        this.parada = parada;

        String dia = DataHoraUtils.getDiaAtual();
        String diaSeguinte = DataHoraUtils.getDiaSeguinte();
        String hora = DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime());

        SimpleSQLiteQuery  query = new SimpleSQLiteQuery("SELECT i.*, e.nome AS 'nomeEmpresa', " +
                "IFNULL(( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1), ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'proximoHorario', " +
                "IFNULL( ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'idProximoHorario', " +
                "IFNULL( ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') < " +
                "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ), " +
                "( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ) ) AS 'horarioAnterior', " +
                "IFNULL( ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') < " +
                "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ), " +
                "( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ) ) AS 'idHorarioAnterior', " +
                "IFNULL( ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') > " +
                "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), " +
                "( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') > " +
                "( IFNULL( ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'horarioSeguinte', " +
                "IFNULL(( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 " +
                "AND TIME(h.nome/1000, 'unixepoch', 'localtime') > ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 " +
                "AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1) " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
                "AND TIME(h.nome/1000, 'unixepoch', 'localtime') > ( IFNULL( ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 " +
                "AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), " +
                "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) ) " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'idHorarioSeguinte', " +
                "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idPartida', ( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'nomePartida', " +
                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'nomeDestino', " +
                "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroPartida', " +
                "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroDestino', " +
                "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroPartida', " +
                "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroDestino', " +
                "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'cidadePartida', " +
                "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'cidadeDestino' " +
                "FROM itinerario i INNER JOIN empresa e ON e.id = i.empresa WHERE i.id IN (SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
                "WHERE p.id = '"+parada.getParada().getId()+"' AND p.id <> (SELECT pi.parada FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
                "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AND proximoHorario IS NOT NULL) " +
                "ORDER BY (SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 )");


        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorarioCompleto(query);

//        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada.getParada().getId(),
//                DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));

        if(parada.getParada().getImagem() != null){
            File foto = new File(getApplication().getFilesDir(), parada.getParada().getImagem());

            if(foto.exists() && foto.canRead()){
                this.foto = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.foto = null;
        }

    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public InfoWindowParadaViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario("", "");
    }

}
