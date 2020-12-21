package br.com.vostre.circular.model.dao;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import android.content.Context;

import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.ClimaCidade;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.FeedbackItinerario;
import br.com.vostre.circular.model.Feriado;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.HistoricoParada;
import br.com.vostre.circular.model.HistoricoSecao;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.ImagemParada;
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
import br.com.vostre.circular.model.Tpr;
import br.com.vostre.circular.model.Tpr2;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.model.UsuarioPreferencia;
import br.com.vostre.circular.model.ViagemItinerario;
import br.com.vostre.circular.model.log.LogItinerario;
import br.com.vostre.circular.model.log.LogParada;
import br.com.vostre.circular.utils.Converters;
import br.com.vostre.circular.utils.DBUtils;

@Database(entities = {Pais.class, Estado.class, Cidade.class, Bairro.class,
        Empresa.class, Parametro.class, Usuario.class, Mensagem.class, MensagemResposta.class,
        Parada.class, PontoInteresse.class, Itinerario.class, ParadaItinerario.class,
        Horario.class, HorarioItinerario.class, SecaoItinerario.class, Onibus.class,
        ParametroInterno.class, ParadaSugestao.class, HistoricoParada.class, UsuarioPreferencia.class,
        HistoricoItinerario.class, Acesso.class, PontoInteresseSugestao.class, TipoProblema.class, Problema.class, Servico.class,
        ViagemItinerario.class, Feriado.class, HistoricoSecao.class, LogItinerario.class, LogParada.class,
        ClimaCidade.class, ImagemParada.class, FeedbackItinerario.class, Tpr.class, Tpr2.class},
        version = 13)
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

    // 2.2.1 - v8 bd = ajustando tabela itinerario - erro na migracao anterior

    // 2.2.2 - v9 bd = ajustando tabela parada_sugestao - erro na migracao anterior

    // 2.2.3 - v10 bd = ajustando tabela tipo_problema - erro na migracao anterior

    // 2.2.4 - v11 bd = forçando recriação do BD para eliminar problemas

    // 2.3.0 - v12 bd = inserindo tabela para registrar viagem, feriado, historico da secao e log de consulta (a ser implementado)
    public abstract ViagemItinerarioDAO viagemItinerarioDAO();
    public abstract FeriadoDAO feriadoDAO();
    public abstract HistoricoSecaoDAO historicoSecaoDAO();
    public abstract LogConsultaDAO logConsultaDAO();

    //2.4.0 - v13 bd = inserindo id de cidade para buscar dados na api de clima, alem de latitude e longitude e dados do clima da cidade
    //                 tabela de imagem da parada para criacao do album de fotos da parada
    //                 tabela de feedback do usuario em relacao ao itinerario (dados inconsistentes, etc.)
    //                 tabelas temporarias 'views' para acelerar consulta de rotas
    public abstract ClimaCidadeDAO climaCidadeDAO();
    public abstract ImagemParadaDAO imagemParadaDAO();
    public abstract FeedbackItinerarioDAO feedbackItinerarioDAO();
    public abstract TemporariasDAO temporariasDAO();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "circular")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            //.allowMainThreadQueries()
                            .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10,
                                    MIGRATION_6_10, MIGRATION_7_10, MIGRATION_8_10, MIGRATION_9_10, MIGRATION_11_12, MIGRATION_12_13)
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'acesso' ('id' TEXT NOT NULL, 'identificadorUnico' TEXT NOT NULL, 'dataCriacao' INTEGER NOT NULL, 'dataValidacao' INTEGER NOT NULL, PRIMARY KEY('id'))");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'ponto_interesse_sugestao' ('observacao' TEXT, 'pontoInteresse' TEXT, 'status' INTEGER NOT NULL, 'descricao' TEXT, 'latitude' REAL NOT NULL, 'longitude' REAL NOT NULL, 'imagem' TEXT, 'dataInicial' INTEGER, 'dataFinal' INTEGER, 'imagemEnviada' INTEGER NOT NULL, 'permanente' INTEGER NOT NULL, 'bairro' TEXT NOT NULL, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("ALTER TABLE 'acesso' ADD COLUMN 'versao' TEXT");
        }
    };

    // v2.2.0 - v7 bd
    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'tipo_problema' ('descricao' TEXT, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Problema' ('descricao' TEXT NOT NULL, 'tipoProblema' TEXT NOT NULL, 'lida' INTEGER NOT NULL, 'imagem' TEXT, 'imagemEnviada' INTEGER NOT NULL, 'situacao' INTEGER NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'rua' TEXT");
            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'cep' TEXT");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'mostraRuas' INTEGER DEFAULT 0");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Servico' ('icone' TEXT NOT NULL, 'imagemEnviada' INTEGER NOT NULL, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'servicos' TEXT");

            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'rua' TEXT");
            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'cep' TEXT");
            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'servicos' TEXT");

            database.execSQL("CREATE UNIQUE INDEX 'index_tipo_problema_nome' ON 'tipo_problema' ('nome')");
            database.execSQL("CREATE UNIQUE INDEX 'index_Servico_nome' ON 'servico' ('nome')");
        }
    };

    // v2.2.1 - v8 bd
    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }
    };

    // v2.2.2 - v9 bd
    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'rua' TEXT");
//            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'cep' TEXT");
//            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'servicos' TEXT");
        }
    };

    // migracao direta
    public static final Migration MIGRATION_6_10 = new Migration(6, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'tipo_problema' ('descricao' TEXT, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Problema' ('descricao' TEXT NOT NULL, 'tipoProblema' TEXT NOT NULL, 'lida' INTEGER NOT NULL, 'imagem' TEXT, 'imagemEnviada' INTEGER NOT NULL, 'situacao' INTEGER NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'rua' TEXT");
            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'cep' TEXT");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'mostraRuas' INTEGER DEFAULT 0");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Servico' ('icone' TEXT NOT NULL, 'imagemEnviada' INTEGER NOT NULL, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'servicos' TEXT");

            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'rua' TEXT");
            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'cep' TEXT");
            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'servicos' TEXT");

            database.execSQL("CREATE UNIQUE INDEX 'index_tipo_problema_nome' ON 'tipo_problema' ('nome')");
            database.execSQL("CREATE UNIQUE INDEX 'index_Servico_nome' ON 'servico' ('nome')");
        }
    };

    public static final Migration MIGRATION_7_10 = new Migration(7, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'tipo_problema' ('descricao' TEXT, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Problema' ('descricao' TEXT NOT NULL, 'tipoProblema' TEXT NOT NULL, 'lida' INTEGER NOT NULL, 'imagem' TEXT, 'imagemEnviada' INTEGER NOT NULL, 'situacao' INTEGER NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
//            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'rua' TEXT");
//            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'cep' TEXT");

            database.execSQL("CREATE TABLE 'iti' ('sigla' TEXT, 'tarifa' REAL NOT NULL, 'distancia' REAL, 'tempo' INTEGER, 'acessivel' INTEGER NOT NULL, 'empresa' TEXT NOT NULL, 'observacao' TEXT, 'mostraRuas' INTEGER DEFAULT 0, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("INSERT INTO 'iti' ('sigla', 'tarifa', 'distancia', 'tempo', 'acessivel', 'empresa', 'observacao', 'mostraRuas', 'id', 'ativo', 'enviado', 'data_cadastro', 'usuario_cadastro', 'ultima_alteracao', 'usuario_ultima_alteracao', 'programado_para') SELECT  'sigla', 'tarifa', 'distancia', 'tempo', 'acessivel', 'empresa', 'observacao', 'mostraRuas', 'id', 'ativo', 'enviado', 'data_cadastro', 'usuario_cadastro', 'ultima_alteracao', 'usuario_ultima_alteracao', 'programado_para' FROM 'Itinerario'");
            database.execSQL("DROP TABLE 'Itinerario'");
            database.execSQL("ALTER TABLE 'iti' RENAME TO 'Itinerario'");

            database.execSQL("CREATE TABLE IF NOT EXISTS 'Servico' ('icone' TEXT NOT NULL, 'imagemEnviada' INTEGER NOT NULL, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
//            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'servicos' TEXT");

//            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'rua' TEXT");
//            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'cep' TEXT");
//            database.execSQL("ALTER TABLE 'parada_sugestao' ADD COLUMN 'servicos' TEXT");

//            database.execSQL("CREATE UNIQUE INDEX 'index_tipo_problema_nome' ON 'tipo_problema' ('nome')");
//            database.execSQL("CREATE UNIQUE INDEX 'index_Servico_nome' ON 'servico' ('nome')");
        }
    };

    public static final Migration MIGRATION_8_10 = new Migration(8, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'tipo_problema' ('descricao' TEXT, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Problema' ('descricao' TEXT NOT NULL, 'tipoProblema' TEXT NOT NULL, 'lida' INTEGER NOT NULL, 'imagem' TEXT, 'imagemEnviada' INTEGER NOT NULL, 'situacao' INTEGER NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
//            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'rua' TEXT");
//            database.execSQL("ALTER TABLE 'parada' ADD COLUMN 'cep' TEXT");
//            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'mostraRuas' INTEGER DEFAULT 0");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Servico' ('icone' TEXT NOT NULL, 'imagemEnviada' INTEGER NOT NULL, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");

        }
    };

    // v2.2.3 - v10 bd
    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }
    };

    // v2.2.4 - v11 bd - NENHUM PROCESSO DE ATUALIZAÇÃO, PARA FORÇAR A RECRIACAO DO BANCO DE DADOS E ELIMINAR TODOS OS PROBLEMAS DE MIGRACAO DA VERSAO 2.2

    // v2.3.0 - v12 bd
    public static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'viagem_itinerario' ('itinerario' TEXT NOT NULL, 'trajeto' TEXT NOT NULL, 'horaInicial' INTEGER, 'horaFinal' INTEGER, 'trajetoEnviado' INTEGER NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS 'index_viagem_itinerario_itinerario_trajeto' ON 'viagem_itinerario' ('itinerario', 'trajeto')");

            database.execSQL("CREATE TABLE IF NOT EXISTS 'Feriado' ('data' INTEGER NOT NULL, 'cidade' TEXT, 'tipo' INTEGER NOT NULL, 'descricao' TEXT, 'nome' TEXT NOT NULL, 'slug' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");

            database.execSQL("CREATE TABLE IF NOT EXISTS 'historico_secao' ('secao' TEXT NOT NULL, 'tarifa' REAL NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS 'index_historico_secao_secao_tarifa' ON 'historico_secao' ('secao', 'tarifa')");

            database.execSQL("ALTER TABLE 'parada_itinerario' ADD COLUMN 'distanciaSeguinteMetros' REAL");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'distanciaMetros' REAL");

            database.execSQL("DROP INDEX IF EXISTS 'index_Parada_nome_bairro'");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS 'index_Parada_nome_bairro_sentido' ON 'parada' ('nome', 'bairro', 'sentido')");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'aliasBairroPartida' TEXT");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'aliasCidadePartida' TEXT");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'aliasBairroDestino' TEXT");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'aliasCidadeDestino' TEXT");

            database.execSQL("CREATE TABLE IF NOT EXISTS 'log_itinerario' ('partida' TEXT, 'destino' TEXT, 'itinerario' TEXT, 'linha' TEXT, 'data_inicio' INTEGER NOT NULL, 'data_fim' INTEGER NOT NULL, 'tipo' TEXT NOT NULL, 'local' TEXT NOT NULL, 'uid' TEXT NOT NULL, 'versao' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'log_parada' ('parada' TEXT NOT NULL, 'data_inicio' INTEGER NOT NULL, 'data_fim' INTEGER NOT NULL, 'tipo' TEXT NOT NULL, 'local' TEXT NOT NULL, 'uid' TEXT NOT NULL, 'versao' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");

            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'paradaInicial' TEXT");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'paradaFinal' TEXT");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'totalParadas' INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE 'itinerario' ADD COLUMN 'trajeto' TEXT");
        }
    };

    // v2.4.0 - v13 bd
    public static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'cidade' ADD COLUMN 'id_clima' INTEGER");
            database.execSQL("ALTER TABLE 'cidade' ADD COLUMN 'latitude' REAL");
            database.execSQL("ALTER TABLE 'cidade' ADD COLUMN 'longitude' REAL");

            database.execSQL("CREATE TABLE IF NOT EXISTS 'clima_cidade' ('idClima' TEXT NOT NULL, 'clima' TEXT NOT NULL, 'icone' TEXT NOT NULL, 'descricao' TEXT NOT NULL, 'temperatura' REAL NOT NULL, 'sensacao' REAL NOT NULL, 'tempMinima' REAL NOT NULL, 'tempMaxima' REAL NOT NULL, 'umidade' REAL NOT NULL, 'nascerDoSol' INTEGER NOT NULL, 'porDoSol' INTEGER NOT NULL, 'cidade' TEXT NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'imagem_parada' ('parada' TEXT NOT NULL, 'imagem' TEXT NOT NULL, 'descricao' TEXT, 'status' INTEGER NOT NULL, 'imagemEnviada' INTEGER NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'feedback_itinerario' ('itinerario' TEXT NOT NULL, 'descricao' TEXT NOT NULL, 'imagem' TEXT, 'imagemEnviada' INTEGER NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");

            database.execSQL("ALTER TABLE 'parada_itinerario' ADD COLUMN 'distanciaAcumulada' REAL");

            database.execSQL("DROP TABLE IF EXISTS 'tpr'");
            database.execSQL("DROP TABLE IF EXISTS 'tpr2'");

            database.execSQL("CREATE TABLE IF NOT EXISTS 'tpr' ('sigla' TEXT, 'tarifa' REAL NOT NULL, 'distanciaAcumuladaInicial' REAL NOT NULL, 'distanciaAcumulada' REAL NOT NULL, 'tempo' INTEGER NOT NULL, 'acessivel' INTEGER NOT NULL, 'empresa' TEXT NOT NULL, 'observacao' TEXT, 'mostraRua' INTEGER NOT NULL, 'idItinerario' TEXT NOT NULL, 'idBairroPartida' TEXT NOT NULL, 'bairroPartida' TEXT NOT NULL, 'cidadePartida' TEXT NOT NULL, 'idBairroDestino' TEXT NOT NULL, 'bairroDestino' TEXT NOT NULL, 'cidadeDestino' TEXT NOT NULL, 'distanciaTrechoMetros' REAL, 'tempoTrecho' INTEGER, 'tarifaTrecho' REAL, 'inicio' INTEGER NOT NULL, 'fim' INTEGER NOT NULL, 'aliasBairroPartida' TEXT, 'aliasCidadePartida' TEXT, 'aliasBairroDestino' TEXT, 'aliasCidadeDestino' TEXT, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'tpr2' ('idItinerario' TEXT NOT NULL, 'distanciaMetros' REAL NOT NULL, 'idBairroPartida' TEXT NOT NULL, 'idBairroDestino' TEXT NOT NULL, 'distanciaTrechoMetros' REAL NOT NULL, 'flagTrecho' INTEGER NOT NULL, 'id' TEXT NOT NULL, 'ativo' INTEGER NOT NULL, 'enviado' INTEGER NOT NULL, 'data_cadastro' INTEGER NOT NULL, 'usuario_cadastro' TEXT, 'ultima_alteracao' INTEGER NOT NULL, 'usuario_ultima_alteracao' TEXT, 'programado_para' INTEGER, PRIMARY KEY('id'))");
        }
    };

}
