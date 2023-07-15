create table translation
(
    id                     integer not null
        constraint translation_pk
            primary key,
    eng_sentence_id        integer,
    eng_sentence_text      text,
    eng_sentence_audio_url text,
    vie_sentence_id        integer,
    vie_sentence_text      text
);
