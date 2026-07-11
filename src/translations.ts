export interface LocalizedContent {
  translation: string;
  description: string;
  lipSteps?: string[];
}

export const UKRAINIAN_TRANSLATIONS: Record<string, LocalizedContent> = {
  'i-long': {
    translation: 'вівця',
    description: 'Довгий звук [ и ]. Губи злегка розтягнуті, як при посмішці.'
  },
  'i-short': {
    translation: 'корабель',
    description: 'Короткий, ненапружений звук [ и ].'
  },
  'u-short': {
    translation: 'хороший',
    description: 'Короткий звук [ у ]. Губи злегка округлені.'
  },
  'u-long': {
    translation: 'стріляти',
    description: 'Довгий звук [ у ]. Губи сильно округлені та витягнуті вперед.'
  },
  'e': {
    translation: 'ліво',
    description: 'Короткий звук [ е ]. Рот привідкритий ширше, ніж для [ ɪ ].'
  },
  'schwa': {
    translation: 'вчитель',
    description: 'Нейтральний ненаголошений звук. Найчастіший звук в англійській.'
  },
  'er-long': {
    translation: 'її',
    description: 'Довгий звук, середній між [ о ] та [ е ].'
  },
  'o-long': {
    translation: 'двері',
    description: 'Довгий звук [ о ]. Губи напружені та округлені.'
  },
  'ae': {
    translation: 'капелюх',
    description: 'Широкий звук [ е ]. Нижня щелепа сильно опущена.'
  },
  'v-short': {
    translation: 'вгору',
    description: 'Короткий звук [ а ]. Рот привідкритий, губи нейтральні.'
  },
  'a-long': {
    translation: 'далеко',
    description: 'Глибокий довгий звук [ а ]. Рот широко відкритий.'
  },
  'o-short': {
    translation: 'на',
    description: 'Короткий звук [ о ]. Рот широко відкритий, губи не округлені.'
  },
  'ie': {
    translation: 'тут',
    description: 'Дифтонг. Перехід від [ ɪ ] до нейтрального [ ə ].'
  },
  'ei': {
    translation: 'чекати',
    description: 'Дифтонг. Перехід від [ e ] до [ ɪ ].'
  },
  'ue': {
    translation: 'тур',
    description: 'Дифтонг. Перехід від [ ʊ ] до нейтрального [ ə ].'
  },
  'oi': {
    translation: 'хлопчик',
    description: 'Дифтонг. Перехід від [ ɔ ] до [ ɪ ].'
  },
  'ou': {
    translation: 'шоу',
    description: 'Дифтонг. Перехід від [ ə ] до [ ʊ ].'
  },
  'ea': {
    translation: 'волосся',
    description: 'Дифтонг. Перехід від [ e ] до нейтрального [ ə ].'
  },
  'ai': {
    translation: 'мій',
    description: 'Дифтонг. Перехід від [ a ] до [ ɪ ].'
  },
  'au': {
    translation: 'корова',
    description: 'Дифтонг. Перехід від [ a ] до [ ʊ ].'
  },
  'p': {
    translation: 'ручка',
    description: 'близький до українського звуку [ п ], але енергійніший і з придихом.',
    lipSteps: ['губи закриті', 'тиск', 'різке відкриття']
  },
  'b': {
    translation: 'сумка',
    description: 'Дзвінкий звук [ б ]. Губи змикаються і розмикаються.',
    lipSteps: ['губи закриті', 'тиск', 'різке відкриття']
  },
  't': {
    translation: 'чай',
    description: 'Глухий звук [ т ]. Кінчик язика на альвеолах.'
  },
  'd': {
    translation: 'собака',
    description: 'Дзвінкий звук [ д ]. Кінчик язика на альвеолах.'
  },
  'k': {
    translation: 'машина',
    description: 'Глухий звук [ к ]. Задня частина язика торкається піднебіння.'
  },
  'g': {
    translation: 'йти',
    description: 'Дзвінкий звук [ г ]. Задня частина язика торкається піднебіння.'
  },
  'g-j': {
    translation: 'жираф',
    description: 'Буква G може читатися як [dʒ] перед e, i, y.'
  },
  'ch': {
    translation: 'сир',
    description: 'Глухий звук [ ч ]. Починається як [ t ], переходить в [ ʃ ].'
  },
  'j': {
    translation: 'червень',
    description: 'Дзвінкий звук [ дж ]. Починається як [ d ], переходить в [ ʒ ].'
  },
  'f': {
    translation: 'муха',
    description: 'Глухий звук [ ф ]. Верхні зуби торкаються нижньої губи.'
  },
  'v': {
    translation: 'відео',
    description: 'Дзвінкий звук [ в ]. Upper зуби торкаються нижньої губи.'
  },
  'th-unvoiced': {
    translation: 'думати',
    description: 'Міжзубний глухий звук. Кінчик язика між зубами.'
  },
  'th-voiced': {
    translation: 'це',
    description: 'Міжзубний дзвінкий звук. Кінчик язика між зубами.'
  },
  's': {
    translation: 'море',
    description: 'Глухий звук [ с ]. Кінчик язика за зубами.'
  },
  's-z': {
    translation: 'ніс',
    description: 'Буква S може читатися як [z] між голосними.'
  },
  'z': {
    translation: 'зоопарк',
    description: 'Дзвінкий звук [ з ]. Кінчик язика за зубами.'
  },
  'sh': {
    translation: 'повинен',
    description: 'Глухий звук [ ш ]. Кінчик язика піднятий до піднебіння.'
  },
  'zh': {
    translation: 'телевізор',
    description: 'Дзвінкий звук [ ж ]. Кінчик язика піднятий до піднебіння.'
  },
  'h': {
    translation: 'капелюх',
    description: 'Легкий видих. Схожий на український [ х ], але слабший.'
  },
  'm': {
    translation: 'людина',
    description: 'Носовий звук [ м ]. Губи зімкнуті.',
    lipSteps: ['губи зімкнуті', 'повітря проходить через ніс', 'голосові зв’язки вібрують']
  },
  'n': {
    translation: 'зараз',
    description: 'Носовий звук [ н ]. Кінчик язика на альвеолах.'
  },
  'ng': {
    translation: 'співати',
    description: 'Задньоязиковий носовий звук. Задня частина язика біля піднебіння.'
  },
  'l': {
    translation: 'любов',
    description: 'Звук [ л ]. Кінчик язика на альвеолах.'
  },
  'r': {
    translation: 'червоний',
    description: 'Звук [ р ]. Кінчик язика піднятий до піднебіння, але не торкається його.'
  },
  'w': {
    translation: 'мокрий',
    description: 'Звук [ уе ]. Губи сильно округлені та розмикаються.'
  },
  'y': {
    translation: 'так',
    description: 'Звук [ й ]. Середня частина язика піднята до піднебіння.'
  },
  'letter-c': {
    translation: 'місто',
    description: 'Буква C читається як [s] перед e, i, y.'
  },
  'letter-c-k': {
    translation: 'кіт',
    description: 'Буква C читається як [k] перед a, o, u.'
  },
  'letter-q': {
    translation: 'королева',
    description: 'Буква Q зазвичай зустрічається в поєднанні qu.'
  },
  'letter-x': {
    translation: 'коробка',
    description: 'Буква X часто читається як [ks].'
  },
  'letter-a-long': {
    translation: 'торт',
    description: 'Буква A читається як [eɪ] у відкритому складі.'
  },
  'letter-a-short': {
    translation: 'яблуко',
    description: 'Буква A читається як [æ] у закритому складі.'
  },
  'letter-e-long': {
    translation: 'бджола',
    description: 'Буква E читається як [iː] у відкритому складі.'
  },
  'letter-e-short': {
    translation: 'яйце',
    description: 'Буква E читається як [e] у закритому складі.'
  },
  'letter-i-long': {
    translation: 'велосипед',
    description: 'Буква I читається як [aɪ] у відкритому складі.'
  },
  'letter-i-short': {
    translation: 'корабель',
    description: 'Буква I читається як [ɪ] у закритому складі.'
  },
  'letter-o-long': {
    translation: 'ніс',
    description: 'Буква O читається як [əʊ] у відкритому складі.'
  },
  'letter-o-short': {
    translation: 'апельсин',
    description: 'Буква O читається як [ɒ] у закритому складі.'
  },
  'letter-u-long': {
    translation: 'синій',
    description: 'Буква U читається як [uː] у відкритому складі.'
  },
  'letter-u-short': {
    translation: 'чашка',
    description: 'Буква U читається як [ʌ] у закритому складі.'
  },
  'comb-sio': {
    translation: 'особняк',
    description: 'Поєднання sio часто читається як [ʃn].'
  },
  'comb-ar': {
    translation: 'машина',
    description: 'Поєднання ar читається як довгий [ɑː].'
  },
  'comb-er': {
    translation: 'її',
    description: 'Поєднання er читається як [ɜː].'
  },
  'comb-or': {
    translation: 'двері',
    description: 'Поєднання or читається як [ɔː].'
  },
  'comb-ir': {
    translation: 'птах',
    description: 'Поєднання ir читається як [ɜː].'
  },
  'comb-oo': {
    translation: 'місяць',
    description: 'Поєднання oo часто читається як довгий [uː].'
  },
  'comb-oi': {
    translation: 'монета',
    description: 'Поєднання oi читається як [ɔɪ].'
  },
  'comb-ou': {
    translation: 'будинок',
    description: 'Поєднання ou часто читається як [aʊ].'
  },
  'comb-ear': {
    translation: 'вухо',
    description: 'Поєднання ear може читатися як [ɪə].'
  },
  'comb-ure': {
    translation: 'чистий',
    description: 'Поєднання ure читається як [ʊə].'
  },
  'comb-air': {
    translation: 'повітря',
    description: 'Поєднання air читається як [eə].'
  }
};
