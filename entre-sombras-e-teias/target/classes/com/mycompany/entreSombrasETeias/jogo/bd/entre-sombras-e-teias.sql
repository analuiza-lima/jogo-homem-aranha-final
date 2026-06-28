--
-- PostgreSQL database dump
--

\restrict FSxRJqWOXjX6cBcVHsfuAQBqu2BTF6RrFTlJY0iuVXNb6ObBJISzZEqqEtwdsor

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

-- Started on 2026-06-25 12:42:58

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 224 (class 1259 OID 16464)
-- Name: fases_progresso; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.fases_progresso (
    id_progresso integer NOT NULL,
    id_jogador integer NOT NULL,
    numero_episodio integer NOT NULL,
    bloqueado boolean DEFAULT true
);


ALTER TABLE public.fases_progresso OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16463)
-- Name: fases_progresso_id_progresso_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.fases_progresso_id_progresso_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.fases_progresso_id_progresso_seq OWNER TO postgres;

--
-- TOC entry 4952 (class 0 OID 0)
-- Dependencies: 223
-- Name: fases_progresso_id_progresso_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.fases_progresso_id_progresso_seq OWNED BY public.fases_progresso.id_progresso;


--
-- TOC entry 226 (class 1259 OID 16480)
-- Name: galeria_imagens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.galeria_imagens (
    id_imagem integer NOT NULL,
    id_jogador integer NOT NULL,
    caminho_textura character varying(255) NOT NULL,
    bloqueado boolean DEFAULT true
);


ALTER TABLE public.galeria_imagens OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16479)
-- Name: galeria_imagens_id_imagem_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.galeria_imagens_id_imagem_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.galeria_imagens_id_imagem_seq OWNER TO postgres;

--
-- TOC entry 4953 (class 0 OID 0)
-- Dependencies: 225
-- Name: galeria_imagens_id_imagem_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.galeria_imagens_id_imagem_seq OWNED BY public.galeria_imagens.id_imagem;


--
-- TOC entry 220 (class 1259 OID 16432)
-- Name: jogadores; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.jogadores (
    id_jogador integer NOT NULL,
    nome character varying(50) DEFAULT 'Peter Parker'::character varying NOT NULL,
    xp_atual integer DEFAULT 0 NOT NULL,
    hp_atual integer DEFAULT 100 NOT NULL,
    nivel_atual integer DEFAULT 1 NOT NULL
);


ALTER TABLE public.jogadores OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16431)
-- Name: jogadores_id_jogador_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.jogadores_id_jogador_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.jogadores_id_jogador_seq OWNER TO postgres;

--
-- TOC entry 4954 (class 0 OID 0)
-- Dependencies: 219
-- Name: jogadores_id_jogador_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.jogadores_id_jogador_seq OWNED BY public.jogadores.id_jogador;


--
-- TOC entry 222 (class 1259 OID 16448)
-- Name: viloes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.viloes (
    id_vilao integer NOT NULL,
    nome character varying(50) NOT NULL,
    descricao text,
    vida_maxima integer NOT NULL,
    xp_recompensa integer NOT NULL,
    numero_episodio integer NOT NULL
);


ALTER TABLE public.viloes OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16447)
-- Name: viloes_id_vilao_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.viloes_id_vilao_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.viloes_id_vilao_seq OWNER TO postgres;

--
-- TOC entry 4955 (class 0 OID 0)
-- Dependencies: 221
-- Name: viloes_id_vilao_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.viloes_id_vilao_seq OWNED BY public.viloes.id_vilao;


--
-- TOC entry 4776 (class 2604 OID 16467)
-- Name: fases_progresso id_progresso; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fases_progresso ALTER COLUMN id_progresso SET DEFAULT nextval('public.fases_progresso_id_progresso_seq'::regclass);


--
-- TOC entry 4778 (class 2604 OID 16483)
-- Name: galeria_imagens id_imagem; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.galeria_imagens ALTER COLUMN id_imagem SET DEFAULT nextval('public.galeria_imagens_id_imagem_seq'::regclass);


--
-- TOC entry 4770 (class 2604 OID 16435)
-- Name: jogadores id_jogador; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jogadores ALTER COLUMN id_jogador SET DEFAULT nextval('public.jogadores_id_jogador_seq'::regclass);


--
-- TOC entry 4775 (class 2604 OID 16451)
-- Name: viloes id_vilao; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.viloes ALTER COLUMN id_vilao SET DEFAULT nextval('public.viloes_id_vilao_seq'::regclass);


--
-- TOC entry 4944 (class 0 OID 16464)
-- Dependencies: 224
-- Data for Name: fases_progresso; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.fases_progresso VALUES (1, 1, 1, false);
INSERT INTO public.fases_progresso VALUES (2, 1, 2, true);
INSERT INTO public.fases_progresso VALUES (3, 1, 3, true);
INSERT INTO public.fases_progresso VALUES (4, 1, 4, true);
INSERT INTO public.fases_progresso VALUES (5, 1, 5, true);
INSERT INTO public.fases_progresso VALUES (6, 1, 6, true);


--
-- TOC entry 4946 (class 0 OID 16480)
-- Dependencies: 226
-- Data for Name: galeria_imagens; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.galeria_imagens VALUES (1, 1, 'img1.png', true);
INSERT INTO public.galeria_imagens VALUES (2, 1, 'img2.png', true);
INSERT INTO public.galeria_imagens VALUES (3, 1, 'img3.png', true);
INSERT INTO public.galeria_imagens VALUES (4, 1, 'img4.png', true);
INSERT INTO public.galeria_imagens VALUES (5, 1, 'img5.png', true);
INSERT INTO public.galeria_imagens VALUES (6, 1, 'img6.png', true);
INSERT INTO public.galeria_imagens VALUES (7, 1, 'img7.png', true);
INSERT INTO public.galeria_imagens VALUES (8, 1, 'img8.png', true);


--
-- TOC entry 4940 (class 0 OID 16432)
-- Dependencies: 220
-- Data for Name: jogadores; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.jogadores VALUES (1, 'Peter Parker', 0, 100, 1);


--
-- TOC entry 4942 (class 0 OID 16448)
-- Dependencies: 222
-- Data for Name: viloes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.viloes VALUES (1, 'Abutre', 'Adrian Toomes utiliza tecnologia para voar e cometer roubos.', 200, 150, 1);
INSERT INTO public.viloes VALUES (2, 'Shocker', 'Herman Schultz usa luvas que disparam vibrações destrutivas.', 350, 300, 2);
INSERT INTO public.viloes VALUES (3, 'Lagarto', 'Dr. Curt Connors se transforma em uma criatura reptiliana.', 500, 450, 3);
INSERT INTO public.viloes VALUES (4, 'Electro', 'Max Dillon controla eletricidade. Seus ataques ocupam espaço.', 650, 600, 4);
INSERT INTO public.viloes VALUES (5, 'Doutor Octopus', 'Otto Octavius é um gênio com tentáculos mecânicos.', 850, 900, 5);
INSERT INTO public.viloes VALUES (6, 'Duende Verde', 'Norman Osborn é o maior inimigo do Homem-Aranha. Caótico.', 1000, 1000, 6);


--
-- TOC entry 4956 (class 0 OID 0)
-- Dependencies: 223
-- Name: fases_progresso_id_progresso_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.fases_progresso_id_progresso_seq', 6, true);


--
-- TOC entry 4957 (class 0 OID 0)
-- Dependencies: 225
-- Name: galeria_imagens_id_imagem_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.galeria_imagens_id_imagem_seq', 8, true);


--
-- TOC entry 4958 (class 0 OID 0)
-- Dependencies: 219
-- Name: jogadores_id_jogador_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.jogadores_id_jogador_seq', 1, true);


--
-- TOC entry 4959 (class 0 OID 0)
-- Dependencies: 221
-- Name: viloes_id_vilao_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.viloes_id_vilao_seq', 6, true);


--
-- TOC entry 4787 (class 2606 OID 16473)
-- Name: fases_progresso fases_progresso_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fases_progresso
    ADD CONSTRAINT fases_progresso_pkey PRIMARY KEY (id_progresso);


--
-- TOC entry 4789 (class 2606 OID 16489)
-- Name: galeria_imagens galeria_imagens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.galeria_imagens
    ADD CONSTRAINT galeria_imagens_pkey PRIMARY KEY (id_imagem);


--
-- TOC entry 4781 (class 2606 OID 16446)
-- Name: jogadores jogadores_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jogadores
    ADD CONSTRAINT jogadores_pkey PRIMARY KEY (id_jogador);


--
-- TOC entry 4783 (class 2606 OID 16462)
-- Name: viloes viloes_numero_episodio_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.viloes
    ADD CONSTRAINT viloes_numero_episodio_key UNIQUE (numero_episodio);


--
-- TOC entry 4785 (class 2606 OID 16460)
-- Name: viloes viloes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.viloes
    ADD CONSTRAINT viloes_pkey PRIMARY KEY (id_vilao);


--
-- TOC entry 4790 (class 2606 OID 16474)
-- Name: fases_progresso fases_progresso_id_jogador_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fases_progresso
    ADD CONSTRAINT fases_progresso_id_jogador_fkey FOREIGN KEY (id_jogador) REFERENCES public.jogadores(id_jogador) ON DELETE CASCADE;


--
-- TOC entry 4791 (class 2606 OID 16490)
-- Name: galeria_imagens galeria_imagens_id_jogador_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.galeria_imagens
    ADD CONSTRAINT galeria_imagens_id_jogador_fkey FOREIGN KEY (id_jogador) REFERENCES public.jogadores(id_jogador) ON DELETE CASCADE;


-- Completed on 2026-06-25 12:43:42

--
-- PostgreSQL database dump complete
--

\unrestrict FSxRJqWOXjX6cBcVHsfuAQBqu2BTF6RrFTlJY0iuVXNb6ObBJISzZEqqEtwdsor