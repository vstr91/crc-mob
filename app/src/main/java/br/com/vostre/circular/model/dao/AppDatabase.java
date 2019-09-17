package br.com.vostre.circular.model.dao;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.HistoricoParada;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.MensagemResposta;
import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.PontoInteresseSugestao;
import br.com.vostre.circular.model.Problema;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.Servico;
import br.com.vostre.circular.model.TipoProblema;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.model.UsuarioPreferencia;
import br.com.vostre.circular.utils.Converters;

@Database(entities = {Pais.class, Estado.class, Cidade.class, Bairro.class,
        Empresa.class, Parametro.class, Usuario.class, Mensagem.class, MensagemResposta.class,
        Parada.class, PontoInteresse.class, Itinerario.class, ParadaItinerario.class,
        Horario.class, HorarioItinerario.class, SecaoItinerario.class, Onibus.class,
        ParametroInterno.class, ParadaSugestao.class, HistoricoParada.class, UsuarioPreferencia.class,
        HistoricoItinerario.class, Acesso.class, PontoInteresseSugestao.class, TipoProblema.class, Problema.class, Servico.class},
        version = 7)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract PaisDAO paisDAO();
    public abstract EstadoDAO estadoDAO();
    public abstract CidadeDAO cidadeDAO();
    public abstract BairroDAO bairroDAO();
    public abstract EmpresaDAO empresaDAO();
    public abstract ParametroDAO parametroDAO();
    public abstract UsuarioDAO usuarioDAO();
    public abstract MensagemDAO mensagemDAO();
    public abstract MensagemRespostaDAO mensagemRespostaDAO();
    public abstract ParadaDAO paradaDAO();
    public abstract PontoInteresseDAO pontoInteresseDAO();
    public abstract ItinerarioDAO itinerarioDAO();
    public abstract ParadaItinerarioDAO paradaItinerarioDAO();
    public abstract HorarioDAO horarioDAO();
    public abstract HorarioItinerarioDAO horarioItinerarioDAO();
    public abstract SecaoItinerarioDAO secaoItinerarioDAO();
    public abstract OnibusDAO onibusDAO();
    public abstract ParametroInternoDAO parametroInternoDAO();

    public abstract ParadaSugestaoDAO paradaSugestaoDAO();
    public abstract HistoricoParadaDAO historicoParadaDAO();

    public abstract UsuarioPreferenciaDAO usuarioPreferenciaDAO();
    public abstract HistoricoItinerarioDAO historicoItinerarioDAO();

    // 2.0.0-b.1.2
    public abstract AcessoDAO acessoDAO();

    // 2.1.0
    public abstract PontoInteresseSugestaoDAO pontoInteresseSugestaoDAO();

    // 2.2.0 - v7 bd
    public abstract TipoProblemaDAO tipoProblemaDAO();
    public abstract ProblemaDAO problemaDAO();
    public abstract ServicoDAO servicoDAO();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "circular")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            //.allowMainThreadQueries()
                            .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'acesso' ('id' TEXT NOT NULL, 'identificadorUnico' TEXT NOT NULL, 'dataCriacao' INTEGER NOT NULL, 'dataValidacao' INTEGER NOT NULL, PRIMARY KEY('id'))");
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'ponto_interesse_sugestao' ('observacao' TEXT, 'pontoInteresse' TEXT, 'status' INTEGER NOT NULL, 'descricao' TEXT, 'latitude' REAL NOT NULL, 'longitude' REAL NOT NULL, 'imagem' TEXT, 'dataInicial' INTEGER, 'dataFinal' INTEGER, 'imagemEnviada' INTEGER NOT NULL, 'permanente' INTEGER NOT NULL, 'bairro' TEXT NOT NULL, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("ALTER TABLE 'acesso' ADD COLUMN 'versao' TEXT");
        }
    };

    // v2.2.0 - v7 bd
    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'tipo_problema' ('descricao' TEXT, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'problema' ('descricao' TEXT NOT NULL, 'tipoProblema' TEXT NOT NULL, 'lida' INTEGER NOT NULL, 'imagem' TEXT, 'imagemEnviada' INTEGER NOT NULL, 'situacao' INTEGER NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'rua' TEXT");
            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'cep' TEXT");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'mostraRuas' INTEGER NOT NULL");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'servico' ('icone' TEXT NOT NULL, 'imagemEnviada' INTEGER NOT NULL, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'servicos' TEXT");
        }
    };

}
