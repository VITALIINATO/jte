import React, { useState, useCallback, useMemo, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { 
  Code, Copy, Check, X, Home, Mic, Type, Gamepad2, 
  CheckCircle, ChevronRight, Settings, Moon, Sun, 
  RotateCcw, ShieldAlert, ChevronLeft, Languages
} from 'lucide-react';
import { PHONEMES } from './constants';
import { Phoneme } from './types';
import { KOTLIN_CODE } from './kotlin-code';
import { UKRAINIAN_TRANSLATIONS } from './translations';

// --- HELPER COMPONENTS ---

const LipAnimation = ({ steps, activeStep }: { steps: string[], activeStep: number }) => {
  return (
    <div className="flex flex-col items-center gap-4 py-6">
      <div className="relative w-48 h-48 bg-white/5 rounded-[48px] border border-white/10 flex items-center justify-center overflow-hidden">
        <motion.div
          key={activeStep}
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="text-4xl font-black text-green-500 italic uppercase"
        >
          {activeStep === 0 && "👄"}
          {activeStep === 1 && "💨"}
          {activeStep === 2 && "😲"}
        </motion.div>
        
        {/* Animated Lip simulation */}
        <div className="absolute inset-0 flex items-center justify-center pointer-events-none opacity-20">
          <motion.div 
            animate={{ 
              height: activeStep === 0 ? 10 : (activeStep === 1 ? 20 : 60),
              width: activeStep === 0 ? 80 : 100,
              borderRadius: activeStep === 2 ? "100%" : "40px"
            }}
            className="border-4 border-green-500 w-24 h-10"
          />
        </div>
      </div>
      
      <div className="flex flex-col items-center gap-2">
        {steps.map((step, i) => (
          <motion.div
            key={i}
            initial={{ opacity: 0.3 }}
            animate={{ opacity: i === activeStep ? 1 : 0.3, x: i === activeStep ? 0 : -10 }}
            className={`text-sm font-bold uppercase tracking-widest ${i === activeStep ? 'text-green-500' : 'text-white/20'}`}
          >
            {i + 1}. {step}
          </motion.div>
        ))}
      </div>
    </div>
  );
};

const LessonHeader = ({ 
  title, 
  onBack, 
  step, 
  totalScreens 
}: { 
  title: string, 
  onBack: () => void, 
  step: number,
  totalScreens: number
}) => {
  return (
    <div className="w-full flex items-center justify-between p-4 border-b border-white/10 shrink-0 bg-[#1A1A1A]">
      <button onClick={onBack} className="p-2 text-white/60 hover:text-white transition-colors">
        <ChevronLeft className="w-6 h-6" />
      </button>
      
      <div className="flex flex-col items-center flex-1 mx-2">
        <h2 className="text-xs font-black text-green-500 uppercase tracking-tighter italic text-center leading-tight">
          {title}
        </h2>
        <p className="text-[10px] text-white/40 mt-0.5">
          Шаг {step + 1} из {totalScreens}
        </p>
      </div>

      <div className="flex gap-1">
        {Array.from({ length: totalScreens }).map((_, i) => (
          <div 
            key={i} 
            className={`w-1.5 h-1.5 rounded-full transition-all duration-300 ${
              i === step ? 'bg-green-500 scale-125' : 'bg-white/10'
            }`} 
          />
        ))}
      </div>
    </div>
  );
};

// --- PRACTICE SCREEN ---

const SYLLABLE_EXAMPLES: Record<string, string[]> = {
  "b": ["[ba:] – [bɔ] - [bu:] -[bæ] – [bi:] ", "[a:b] – [ɔ:b] - [u:b] -[æb] – [i:b]"],
  "p": ["[pa:] – [pɔ] - [pu:] -[pæ] – [pi:] ", "[a:p] – [ɔp] - [u:p] -[æp] – [i:p]"],
  "f": ["[fa:] – [fɔ] - [fu:] -[fæ] – [fi:] ", "[a:f] – [ɔf] - [u:f] -[æf] – [i:f]"],         
  "v": ["[va:] – [vɔ] - [vu:] -[væ] – [vi:] ", "[a:v] – [ɔv] - [u:v] -[æv] – [i:v]"],
  "m": ["[ma:] – [mɔ:] - [mu:] -[mæ] – [mi:] ", "[a:m] – [ɔ:m] - [u:m] -[æm] – [i:m]"]
};

const transcriptionMap: Record<string, string> = {
  // b
  'ba:': 'bah',
  'bɔ': 'bore',
  'bu:': 'boo',
  'bæ': 'ba',
  'bi:': 'bee',
  'a:': 'ah',
  'ɔ:': 'or',
  'u:': 'oo',
  'æ': 'aa',
  'i:': 'ee',
  'a:b': 'ahb',
  'ɔ:b': 'orb',
  'u:b': 'oob',
  'æb': 'ab',
  'i:b': 'eeb',
  // p
  'pa:': 'pah',
  'pɔ': 'pore',
  'pu:': 'poo',
  'pæ': 'pa',
  'pi:': 'pee',
  'a:p': 'ahp',
  'ɔp': 'op',
  'u:p': 'oop',
  'æp': 'ap',
  'i:p': 'eep',
  // f
  'fa:': 'fah',
  'fɔ': 'fore',
  'fu:': 'foo',
  'fæ': 'fa',
  'fi:': 'fee',
  'a:f': 'ahf',
  'ɔf': 'off',
  'u:f': 'oof',
  'æf': 'af',
  'i:f': 'eef',
  'ɔ': 'o',
  // v
  'va:': 'vah',
  'vɔ': 'vore',
  'vu:': 'voo',
  'væ': 'va',
  'vi:': 'vee',
  'a:v': 'ahv',
  'ɔv': 'ov',
  'u:v': 'oov',
  'æv': 'av',
  'i:v': 'eev',
  // m
  'ma:': 'mah',
  'mɔ:': 'more',
  'mu:': 'moo',
  'mæ': 'ma',
  'm:': 'mmm',
  'a:m': 'ahm',
  'ɔ:m': 'orm',
  'u:m': 'oom',
  'æm': 'am',
  'i:m': 'eem'
};

const PracticeScreen = ({ 
  onBack, 
  activeLesson, 
  setActiveLesson, 
  lessonStep, 
  setLessonStep, 
  completedLessons, 
  setCompletedLessons,
  isDarkMode,
  onPhonemeClick,
  language,
  localizedPhonemes
}: any) => {
  const [isConfirmed, setIsConfirmed] = useState(false);
  const [activeLipStep, setActiveLipStep] = useState(0);
  const [slideDirection, setSlideDirection] = useState<'forward' | 'backward'>('forward');

  // Lesson data mapping (1 to 15 fully matching Kotlin app lessonConfig)
  const lessonsData: Record<number, { title: string, phonemes: Phoneme[] }> = useMemo(() => ({
    1: { title: 'Урок 1. Знакомство со звуком [b]', phonemes: [localizedPhonemes.find(p => p.id === 'b')!].filter(Boolean) },
    2: { title: 'Урок 2. Знакомство со звуком [p]', phonemes: [localizedPhonemes.find(p => p.id === 'p')!].filter(Boolean) },
    3: { title: 'Урок 3. Знакомство со звуком [v]', phonemes: [localizedPhonemes.find(p => p.id === 'v')!].filter(Boolean) },
    4: { title: 'Урок 4. Знакомство со звуком [f]', phonemes: [localizedPhonemes.find(p => p.id === 'f')!].filter(Boolean) },
    5: { title: 'Урок 5. Знакомство со звуком [m]', phonemes: [localizedPhonemes.find(p => p.id === 'm')!].filter(Boolean) },
    6: { title: 'Урок 6. Знакомство со звуками', phonemes: [localizedPhonemes.find(p => p.id === 's')!, localizedPhonemes.find(p => p.id === 'z')!].filter(Boolean) },
    7: { title: 'Урок 7. Знакомство со звуками', phonemes: [localizedPhonemes.find(p => p.id === 'g')!, localizedPhonemes.find(p => p.id === 'k')!].filter(Boolean) },
    8: { title: 'Урок 8. Знакомство со звуками', phonemes: ['t', 'd', 'n', 'l'].map(id => localizedPhonemes.find(p => p.id === id)!).filter(Boolean) },
    9: { title: 'Урок 9. Знакомство со звуками', phonemes: ['h', 'w', 'r', 'y', 'j'].map(id => localizedPhonemes.find(p => p.id === id)!).filter(Boolean) },
    10: { title: 'Урок 10. Знакомство со звуками', phonemes: ['ch', 'sh', 'zh', 'th-unvoiced', 'th-voiced', 'ng'].map(id => localizedPhonemes.find(p => p.id === id)!).filter(Boolean) },
    11: { title: 'Урок 11. Знакомство со звуками', phonemes: ['ei', 'ae', 'i-long', 'e'].map(id => localizedPhonemes.find(p => p.id === id)!).filter(Boolean) },
    12: { title: 'Урок 12. Знакомство со звуками', phonemes: ['ai', 'i-short', 'ou', 'o-short'].map(id => localizedPhonemes.find(p => p.id === id)!).filter(Boolean) },
    13: { title: 'Урок 13. Знакомство со звуками', phonemes: ['u-long', 'v-short', 'a-long', 'schwa'].map(id => localizedPhonemes.find(p => p.id === id)!).filter(Boolean) },
    14: { title: 'Урок 14. Знакомство со звуками', phonemes: ['o-long', 'er-long', 'u-short', 'oi', 'au'].map(id => localizedPhonemes.find(p => p.id === id)!).filter(Boolean) },
    15: { title: 'Урок 15. Знакомство со звуками', phonemes: ['ie', 'ue', 'ea'].map(id => localizedPhonemes.find(p => p.id === id)!).filter(Boolean) },
  }), [localizedPhonemes]);

  if (activeLesson === null) {
    return (
      <div className="flex-1 flex flex-col p-4 overflow-hidden">
        <h2 className="text-xl font-black text-green-500 uppercase italic mb-4">
          {language === 'ua' ? "Уроки" : "Уроки"}
        </h2>
        <div className="flex-1 overflow-y-auto space-y-2 no-scrollbar">
          {Object.entries(lessonsData).map(([id, data]) => {
            const lessonNum = parseInt(id);
            const isCompleted = lessonNum <= completedLessons;
            const isLocked = lessonNum > completedLessons + 1;
            return (
              <button
                key={id}
                disabled={isLocked}
                onClick={() => setActiveLesson(lessonNum)}
                className={`w-full p-4 rounded-2xl border flex items-center justify-between transition-all ${
                  isCompleted ? 'bg-green-600/10 border-green-500/30' : 
                  isLocked ? 'opacity-30 grayscale cursor-not-allowed border-white/5' : 
                  'bg-white/5 border-white/10 hover:bg-white/10'
                }`}
              >
                <div className="flex items-center gap-3">
                  <div className={`w-8 h-8 rounded-lg flex items-center justify-center font-bold ${isCompleted ? 'bg-green-500 text-white' : 'bg-white/10 text-white/40'}`}>
                    {id}
                  </div>
                  <div className="text-left">
                    <p className="text-[10px] font-bold text-white/40 uppercase">
                      {language === 'ua' ? "Урок" : "Урок"} {id}
                    </p>
                    <p className={`text-sm font-bold italic uppercase ${isCompleted ? 'text-green-500' : 'text-white'}`}>
                      [{data.phonemes.map(p => p.symbol).join(', ')}]
                    </p>
                  </div>
                </div>
                {isCompleted && <CheckCircle className="w-5 h-5 text-green-500" />}
              </button>
            );
          })}
        </div>
      </div>
    );
  }

  const lesson = lessonsData[activeLesson];
  // Calculate current phoneme and screen
  // 3 screens per phoneme: 0: Lip steps, 1: Syllables, 2: Word
  const currentPhonemeIndex = Math.floor(lessonStep / 3);
  const screenInPhoneme = lessonStep % 3;
  const currentPhoneme = lesson.phonemes[currentPhonemeIndex];
  const isLessonComplete = lessonStep >= lesson.phonemes.length * 3;

  const nextStep = () => {
    setSlideDirection('forward');
    if (activeLesson <= 3 && screenInPhoneme === 0) {
      if (activeLipStep < 2) {
        setActiveLipStep(prev => prev + 1);
        return;
      }
    }
    setActiveLipStep(0);
    setLessonStep(prev => prev + 1);
  };

  const prevStep = () => {
    setSlideDirection('backward');
    if (lessonStep === 0 && activeLipStep === 0) {
      setActiveLesson(null);
      return;
    }
    if (activeLesson <= 3 && screenInPhoneme === 0 && activeLipStep > 0) {
      setActiveLipStep(prev => prev - 1);
      return;
    }
    
    const prevStepIndex = Math.max(0, lessonStep - 1);
    const prevScreenInPhoneme = prevStepIndex % 3;
    if (activeLesson <= 3 && prevScreenInPhoneme === 0) {
      setActiveLipStep(2);
    } else {
      setActiveLipStep(0);
    }
    setLessonStep(prevStepIndex);
  };

  const getSpelling = (phoneme: Phoneme): string => {
    const idMap: Record<string, string> = {
      'i-long': 'ee',
      'i-short': 'i',
      'u-short': 'u',
      'u-long': 'oo',
      'schwa': 'a',
      'er-long': 'ur',
      'o-long': 'or',
      'ae': 'a',
      'v-short': 'u',
      'a-long': 'ah',
      'o-short': 'o',
      'th-unvoiced': 'th',
      'th-voiced': 'th',
      'sh': 'sh',
      'zh': 'sh',
      'ch': 'ch',
      'ng': 'ng',
      'g-j': 'j',
    };
    if (idMap[phoneme.id]) return idMap[phoneme.id];
    const label = phoneme.letterLabel || phoneme.symbol;
    const lower = label.toLowerCase();
    if (lower.length === 2 && lower[0] === lower[1]) {
      return lower[0];
    }
    return lower;
  };

  const playSound = (text: string) => {
    window.speechSynthesis.cancel();
    
    // Check if it matches a phoneme ID
    const p = PHONEMES.find(x => x.id === text);
    let targetName = text;
    if (p) {
      targetName = getSpelling(p);
    }
    
    // Clean to lowercase plain text filename (e.g. "ba", "ab")
    const clean = targetName.replace(/[\[\]ː:]/g, '').toLowerCase().trim();
    
    const audio = new Audio(`/sounds/${clean}.mp3`);
    audio.play().catch(e => {
      console.warn(`Could not play /sounds/${clean}.mp3, trying speech synthesis:`, e);
      // Fallback
      const utterance = new SpeechSynthesisUtterance(clean);
      utterance.lang = 'en-US';
      window.speechSynthesis.speak(utterance);
    });
  };

  if (isLessonComplete) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center p-8 text-center bg-[#1A1A1A]">
        <CheckCircle className="w-24 h-24 text-green-500 mb-6" />
        <h2 className="text-3xl font-black italic uppercase mb-2">
          {language === 'ua' ? "Урок засвоєно!" : "Урок усвоен!"}
        </h2>
        <p className="text-white/40 mb-8">
          {language === 'ua' ? "Ви успішно вивчили звуки:" : "Вы успешно изучили звуки:"} [{lesson.phonemes.map(p => p.symbol).join(', ')}]
        </p>
        
        <button
          onClick={() => {
            setCompletedLessons(Math.max(completedLessons, activeLesson));
            setActiveLesson(null);
            setLessonStep(0);
          }}
          className="w-full py-4 bg-green-600 rounded-2xl font-bold uppercase tracking-widest shadow-lg shadow-green-600/20 mb-4"
        >
          {language === 'ua' ? "Завершити" : "Завершить"}
        </button>
        <button
          onClick={() => {
            setLessonStep(0);
          }}
          className="w-full py-4 bg-transparent border border-white/10 rounded-2xl font-bold uppercase tracking-widest text-white/60 hover:text-white"
        >
          {language === 'ua' ? "Назад (повторити)" : "Назад (повторить)"}
        </button>
      </div>
    );
  }

  const screenTitle = (() => {
    switch (screenInPhoneme) {
      case 0: return language === 'ua' ? "Знайомство зі звуком" : "Знакомство со звуком";
      case 1: return language === 'ua' ? "Склади зі звуком" : "Слоги со звуком";
      default: return language === 'ua' ? "Слова зі звуком" : "Слова со звуком";
    }
  })();

  const splitSyllables = (text: string): string[] => {
    const matches = text.match(/\[[^\]]+\]/g);
    if (matches) {
      return matches.map(m => m.trim());
    }
    return text.split(/[-–]/).map(s => s.trim()).filter(Boolean);
  };

  const getFirstRealChar = (s: string): string => {
    const cleaned = s.replace(/[\[\]]/g, '').trim();
    return cleaned.length > 0 ? cleaned[0].toLowerCase() : '';
  };

  const { column1Syllables, column2Syllables } = (() => {
    const isVowel = ['monophthong', 'diphthong'].includes(currentPhoneme.category) ||
                    currentPhoneme.id.startsWith('letter-a') ||
                    currentPhoneme.id.startsWith('letter-e') ||
                    currentPhoneme.id.startsWith('letter-i') ||
                    currentPhoneme.id.startsWith('letter-o') ||
                    currentPhoneme.id.startsWith('letter-u');
                    
    const spelling = getSpelling(currentPhoneme);
    
    if (isVowel) {
      const consonants = ['b', 'p', 'f', 'v', 'm'];
      return {
        column1Syllables: consonants.map(c => `${c}${spelling}`),
        column2Syllables: consonants.map(c => `${spelling}${c}`)
      };
    } else {
      const vowels = ['a', 'o', 'u', 'e', 'i'];
      return {
        column1Syllables: vowels.map(v => `${spelling}${v}`),
        column2Syllables: vowels.map(v => `${v}${spelling}`)
      };
    }
  })();

  return (
    <div className="flex-1 flex flex-col h-full bg-[#1A1A1A]">
      <LessonHeader 
        title={`${language === 'ua' ? "Урок" : "Урок"} ${activeLesson}. ${screenTitle} [${currentPhoneme.symbol}]`}
        onBack={() => setActiveLesson(null)}
        step={lessonStep}
        totalScreens={lesson.phonemes.length * 3}
      />
      
      <div className="flex-1 overflow-y-auto no-scrollbar p-6 flex flex-col justify-between">
        <AnimatePresence mode="wait">
          <motion.div
            key={lessonStep}
            drag="x"
            dragConstraints={{ left: 0, right: 0 }}
            dragElastic={0.6}
            onDragEnd={(event, info) => {
              const swipeThreshold = 15;
              const velocityThreshold = 100;
              if (info.offset.x < -swipeThreshold || info.velocity.x < -velocityThreshold) {
                nextStep();
              } else if (info.offset.x > swipeThreshold || info.velocity.x > velocityThreshold) {
                prevStep();
              }
            }}
            initial={{ opacity: 0, x: slideDirection === 'forward' ? 20 : -20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: slideDirection === 'forward' ? -20 : 20 }}
            className="flex-1 w-full flex flex-col items-center cursor-grab active:cursor-grabbing touch-pan-y select-none"
          >
            {screenInPhoneme === 0 ? (
              <div className="w-full flex-1 flex flex-col items-center justify-center">
                <button 
                  onClick={() => playSound(currentPhoneme.id)}
                  className="w-20 h-20 rounded-3xl bg-white/5 border border-white/10 flex items-center justify-center mb-6 shadow-xl active:scale-95 transition-all text-3xl font-black text-green-500 italic"
                >
                  [{currentPhoneme.symbol}]
                </button>
                <div className="p-4 bg-white/5 rounded-2xl border border-white/10 mb-8 w-full">
                  <p className="text-xs text-green-500/80 text-center leading-relaxed">
                    {currentPhoneme.description}
                  </p>
                </div>
                
                <LipAnimation 
                  steps={currentPhoneme.lipSteps || (language === 'ua' ? ["Зачиніть губи", "Створіть тиск", "Різко відчиніть"] : ["Закройте губы", "Создайте давление", "Резко откройте"])} 
                  activeStep={activeLipStep} 
                />
              </div>
            ) : screenInPhoneme === 1 ? (
              <div className="w-full flex-1 flex flex-col items-center justify-center">
                <button 
                   onClick={() => playSound(currentPhoneme.id)}
                   className="w-20 h-20 rounded-3xl bg-white/5 border border-white/10 flex items-center justify-center mb-6 text-3xl font-black text-green-500 italic animate-pulse"
                >
                  [{currentPhoneme.symbol}]
                </button>
                <p className="text-green-500 text-xs tracking-[0.2em] font-extrabold uppercase mb-8">
                  {language === 'ua' ? "ВИМОВІТЬ ЦІ СКЛАДИ" : "ПРОИЗНЕСИТЕ ДАННЫЕ СЛОГИ"}
                </p>
                
                <div className="grid grid-cols-2 gap-4 w-full max-w-xs mx-auto">
                  <div className="flex flex-col gap-3">
                    {column1Syllables.map((syllable, idx) => (
                      <button
                        key={`con-cv-${idx}`}
                        onClick={() => playSound(syllable)}
                        className="w-full py-3 px-4 bg-white/5 border border-white/10 rounded-2xl font-bold text-lg text-white text-center hover:bg-white/10 active:scale-95 transition-all"
                      >
                        {syllable}
                      </button>
                    ))}
                  </div>
                  <div className="flex flex-col gap-3">
                    {column2Syllables.map((syllable, idx) => (
                      <button
                        key={`vow-vc-${idx}`}
                        onClick={() => playSound(syllable)}
                        className="w-full py-3 px-4 bg-white/5 border border-white/10 rounded-2xl font-bold text-lg text-white text-center hover:bg-white/10 active:scale-95 transition-all"
                      >
                        {syllable}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            ) : (
              <div className="w-full flex-1 flex flex-col items-center justify-center">
                <div className="text-center mb-12">
                   <h1 className="text-6xl font-black italic uppercase tracking-tighter text-white mb-2">
                     {currentPhoneme.example}
                   </h1>
                   <p className="text-2xl font-bold text-white/40 tracking-widest">{currentPhoneme.transcription}</p>
                   <p className="text-lg italic font-serif text-white/60 mt-2">{currentPhoneme.translation}</p>
                </div>
                
                <div 
                  className="w-48 h-48 rounded-[48px] overflow-hidden border-2 border-white/10 shadow-2xl cursor-pointer"
                  onClick={() => playSound(currentPhoneme.example)}
                >
                  <img src={`https://picsum.photos/seed/${currentPhoneme.example}/400/400`} alt="" className="w-full h-full object-cover" />
                </div>
              </div>
            )}
          </motion.div>
        </AnimatePresence>

        <div className="w-full grid grid-cols-2 gap-4 mt-8">
          <button
            onClick={prevStep}
            className="py-4 bg-white/5 border border-white/10 rounded-2xl font-black uppercase tracking-widest text-white/40"
          >
            {language === 'ua' ? "Назад" : "Назад"}
          </button>
          <button
            onClick={nextStep}
            className="py-4 bg-green-600 rounded-2xl font-black uppercase tracking-widest text-white shadow-lg shadow-green-600/20 animate-bounce"
          >
            {language === 'ua' ? "Вперед" : "Вперед"}
          </button>
        </div>
      </div>
    </div>
  );
};

// --- LANGUAGE SELECT SCREEN ---

const LanguageSelectScreen = ({ onSelect }: { onSelect: (lang: 'ru' | 'ua') => void; key?: any }) => {
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.95 }}
      className="flex-1 flex flex-col items-center justify-center p-8 text-center"
    >
      <div className="w-24 h-24 rounded-full bg-green-500/10 flex items-center justify-center mb-8 border border-green-500/30">
        <Languages className="w-12 h-12 text-green-500" />
      </div>
      
      <h1 className="text-4xl font-extrabold italic uppercase mb-2 tracking-tight">
        Choose Language
      </h1>
      <p className="text-white/40 mb-12 max-w-xs text-sm leading-relaxed">
        Выберите язык объяснений для обучения
        <br />
        Оберіть мову пояснень для навчання
      </p>

      <div className="grid gap-4 w-full max-w-xs relative z-10">
        <button
          onClick={() => onSelect('ru')}
          className="w-full py-5 bg-white/5 hover:bg-white/10 border border-white/10 rounded-2xl font-bold uppercase tracking-widest text-white shadow-lg active:scale-95 transition-all text-center flex flex-col items-center justify-center gap-1 cursor-pointer"
        >
          <span className="text-lg font-black text-white">Русский</span>
          <span className="text-xs text-white/45 font-normal normal-case">Язык объяснений: Русский</span>
        </button>

        <button
          onClick={() => onSelect('ua')}
          className="w-full py-5 bg-white/5 hover:bg-white/10 border border-white/10 rounded-2xl font-bold uppercase tracking-widest text-white shadow-lg active:scale-95 transition-all text-center flex flex-col items-center justify-center gap-1 cursor-pointer"
        >
          <span className="text-lg font-black text-green-500">Українська</span>
          <span className="text-xs text-white/45 font-normal normal-case">Мова пояснень: Українська</span>
        </button>
      </div>
    </motion.div>
  );
};

// --- WELCOME SCREEN ---

const WelcomeScreen = ({ onStart, skipWelcome, setSkipWelcome, isDarkMode, language }: any) => {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="flex-1 flex flex-col items-center justify-center p-8 text-center"
    >
      <div className="w-24 h-24 rounded-full bg-green-500/20 flex items-center justify-center mb-8 border border-green-500/30">
        <Check className="w-12 h-12 text-green-500" />
      </div>
      <h1 className="text-4xl font-black italic uppercase mb-4">
        {language === 'ua' ? "Авторська програма" : "Авторская программа"}
      </h1>
      <p className="text-white/40 mb-12 max-w-xs text-sm leading-relaxed">
        {language === 'ua' 
          ? "Принципи авторської програми засновані на поступовому переході від простого до складного." 
          : "Принципы авторской программы основаны на постепенном переходе от простого к сложному."}
      </p>
      
      <div className="flex items-center gap-3 mb-8 cursor-pointer relative z-10" onClick={() => setSkipWelcome(!skipWelcome)}>
        <div className={`w-6 h-6 rounded-lg border flex items-center justify-center transition-colors ${skipWelcome ? 'bg-green-600 border-green-600' : 'border-white/20'}`}>
          {skipWelcome && <Check className="w-4 h-4 text-white" />}
        </div>
        <span className="text-sm font-bold text-white/60">
          {language === 'ua' ? "Не показувати знову" : "Не показывать снова"}
        </span>
      </div>

      <button
        onClick={onStart}
        className="w-full py-4 bg-green-600 rounded-2xl font-bold uppercase tracking-widest text-white shadow-lg shadow-green-600/20 active:scale-95 transition-all"
      >
        {language === 'ua' ? "Почати програму" : "Начать программу"}
      </button>
    </motion.div>
  );
};

// --- ALPHABET GRID ---

const AlphabetGridScreen = ({ onBack, onPlayLetter, isDarkMode, language }: any) => {
  const alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
  return (
    <div className="flex-1 flex flex-col p-4">
      <div className="flex items-center gap-4 mb-6">
        <button onClick={onBack} className="p-2 text-white/60 hover:text-white">
          <ChevronLeft className="w-6 h-6" />
        </button>
        <h2 className="text-xl font-black text-green-500 uppercase italic">
          {language === 'ua' ? "26 букв алфавіту" : "26 букв алфавита"}
        </h2>
      </div>
      <div className="grid grid-cols-5 gap-2">
        {alphabet.map(letter => (
          <button
            key={letter}
            onClick={() => onPlayLetter(letter)}
            className="aspect-square rounded-xl bg-white/5 border border-white/10 flex items-center justify-center text-xl font-bold text-green-500 hover:bg-white/10"
          >
            {letter}
          </button>
        ))}
      </div>
    </div>
  );
};

// --- SETTINGS DIALOG ---

const SettingsDialog = ({ 
  isDarkMode, 
  onDarkModeChange, 
  language,
  onLanguageChange,
  onDismiss, 
  onResetProgress, 
  onResetSettings,
  onCopyKotlin
}: any) => {
  return (
    <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-6" onClick={onDismiss}>
      <motion.div 
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        className="w-full max-w-sm bg-[#1A1A1A] rounded-[32px] border border-white/10 p-8 space-y-6"
        onClick={e => e.stopPropagation()}
      >
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-black uppercase italic">
            {language === 'ua' ? "Налаштування" : "Настройки"}
          </h2>
          <button onClick={onDismiss} className="p-2 text-white/40 hover:text-white"><X /></button>
        </div>
        <div className="space-y-4">
          {/* Language selection block */}
          <div className="p-4 bg-white/5 rounded-2xl border border-white/10 space-y-3">
            <div className="flex items-center gap-3">
              <Languages className="w-5 h-5 text-green-500" />
              <span className="font-bold text-sm">
                {language === 'ua' ? "Мова пояснень" : "Язык объяснений"}
              </span>
            </div>
            <div className="grid grid-cols-2 gap-2">
              <button
                onClick={() => onLanguageChange('ru')}
                className={`py-2 px-3 rounded-xl font-bold text-xs transition-colors border ${language === 'ru' ? 'bg-green-600 text-white border-green-500 shadow-md shadow-green-600/10' : 'bg-white/5 border-white/10 text-white/60 hover:bg-white/10'}`}
              >
                Русский
              </button>
              <button
                onClick={() => onLanguageChange('ua')}
                className={`py-2 px-3 rounded-xl font-bold text-xs transition-colors border ${language === 'ua' ? 'bg-green-600 text-white border-green-500 shadow-md shadow-green-600/10' : 'bg-white/5 border-white/10 text-white/60 hover:bg-white/10'}`}
              >
                Українська
              </button>
            </div>
          </div>

          <div className="flex items-center justify-between p-4 bg-white/5 rounded-2xl border border-white/10">
            <div className="flex items-center gap-3">
              <Moon className="w-5 h-5 text-blue-400" />
              <span className="font-bold">
                {language === 'ua' ? "Темна тема" : "Темная тема"}
              </span>
            </div>
            <button 
              onClick={() => onDarkModeChange(!isDarkMode)}
              className={`w-12 h-6 rounded-full relative transition-colors ${isDarkMode ? 'bg-green-600' : 'bg-gray-600'}`}
            >
              <div className={`absolute top-1 w-4 h-4 rounded-full bg-white transition-all ${isDarkMode ? 'left-7' : 'left-1'}`} />
            </button>
          </div>

          <button onClick={onCopyKotlin} className="w-full p-4 bg-white/5 rounded-2xl border border-white/10 flex items-center gap-3 font-bold hover:bg-white/10 transition-all">
            <Code className="w-5 h-5 text-green-500" />
            {language === 'ua' ? "Копіювати Kotlin код" : "Копировать Kotlin код"}
          </button>

          <button onClick={onResetProgress} className="w-full p-4 bg-white/5 rounded-2xl border border-white/10 flex items-center gap-3 font-bold text-red-400 hover:bg-red-400/10 transition-all">
            <RotateCcw className="w-5 h-5" />
            {language === 'ua' ? "Скинути прогрес" : "Сбросить прогресс"}
          </button>
          
          <button onClick={onResetSettings} className="w-full p-4 bg-white/5 rounded-2xl border border-white/10 flex items-center gap-3 font-bold text-orange-400 hover:bg-orange-400/10 transition-all">
            <ShieldAlert className="w-5 h-5" />
            {language === 'ua' ? "Скинути все" : "Сбросить всё"}
          </button>
        </div>
      </motion.div>
    </div>
  );
};

// --- MAIN APP COMPONENT ---

export default function App() {
  const [language, setLanguage] = useState<'ru' | 'ua'>(() => (localStorage.getItem('language') || 'ru') as 'ru' | 'ua');
  const [isDarkMode, setIsDarkMode] = useState(true);
  const [skipWelcome, setSkipWelcome] = useState(() => localStorage.getItem('skip_welcome') === 'true');
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [screen, setScreen] = useState(() => {
    const langSet = localStorage.getItem('language') !== null;
    if (!langSet) return 'language_select';
    return skipWelcome ? 'menu' : 'welcome';
  });
  const [activeTab, setActiveTab] = useState('home');
  const [activeLesson, setActiveLesson] = useState<number | null>(null);
  const [lessonStep, setLessonStep] = useState(0);
  const [completedLessons, setCompletedLessons] = useState(() => parseInt(localStorage.getItem('completed_lessons') || '0'));
  const [showKotlinCode, setShowKotlinCode] = useState(false);
  const [copied, setCopied] = useState(false);
  const [selectedPhoneme, setSelectedPhoneme] = useState<Phoneme>(PHONEMES[0]);
  const [activeCategory, setActiveCategory] = useState('СОГЛАСНЫЕ');

  useEffect(() => {
    localStorage.setItem('skip_welcome', skipWelcome.toString());
  }, [skipWelcome]);

  useEffect(() => {
    localStorage.setItem('completed_lessons', completedLessons.toString());
  }, [completedLessons]);

  const handleLanguageChange = (lang: 'ru' | 'ua') => {
    setLanguage(lang);
    localStorage.setItem('language', lang);
  };

  const handleInitialLanguageSelect = (lang: 'ru' | 'ua') => {
    handleLanguageChange(lang);
    setScreen('welcome');
  };

  const localizedPhonemes = useMemo(() => {
    return PHONEMES.map(p => {
      if (language === 'ua') {
        const trans = UKRAINIAN_TRANSLATIONS[p.id];
        if (trans) {
          return {
            ...p,
            translation: trans.translation,
            description: trans.description,
            lipSteps: trans.lipSteps || p.lipSteps
          };
        }
      }
      return p;
    });
  }, [language]);

  const currentSelectedPhoneme = useMemo(() => {
    return localizedPhonemes.find(p => p.id === selectedPhoneme.id) || selectedPhoneme;
  }, [localizedPhonemes, selectedPhoneme]);

  const categories = useMemo(() => ({
    'СОГЛАСНЫЕ': localizedPhonemes.filter(p => !['monophthong', 'diphthong'].includes(p.category)),
    'ГЛАСНЫЕ': localizedPhonemes.filter(p => ['monophthong', 'diphthong'].includes(p.category))
  }), [localizedPhonemes]);

  const handleBack = () => {
    if (activeLesson !== null) {
      setActiveLesson(null);
      setLessonStep(0);
      return;
    }
    setScreen('menu');
    setActiveTab('home');
  };

  const getSpellingLocal = (phoneme: any): string => {
    const idMap: Record<string, string> = {
      'i-long': 'ee',
      'i-short': 'i',
      'u-short': 'u',
      'u-long': 'oo',
      'schwa': 'a',
      'er-long': 'ur',
      'o-long': 'or',
      'ae': 'a',
      'v-short': 'u',
      'a-long': 'ah',
      'o-short': 'o',
      'th-unvoiced': 'th',
      'th-voiced': 'th',
      'sh': 'sh',
      'zh': 'sh',
      'ch': 'ch',
      'ng': 'ng',
      'g-j': 'j',
    };
    if (idMap[phoneme.id]) return idMap[phoneme.id];
    const label = phoneme.letterLabel || phoneme.symbol;
    const lower = label.toLowerCase();
    if (lower.length === 2 && lower[0] === lower[1]) {
      return lower[0];
    }
    return lower;
  };

  const playSound = (text: string) => {
    window.speechSynthesis.cancel();
    
    // Check if it matches a phoneme ID
    const p = PHONEMES.find(x => x.id === text);
    let targetName = text;
    if (p) {
      targetName = getSpellingLocal(p);
    }
    
    // Clean to lowercase plain text filename (e.g. "ba", "ab")
    const clean = targetName.replace(/[\[\]ː:]/g, '').toLowerCase().trim();
    
    const audio = new Audio(`/sounds/${clean}.mp3`);
    audio.play().catch(e => {
      console.warn(`Could not play /sounds/${clean}.mp3, trying speech synthesis:`, e);
      // Fallback
      const utterance = new SpeechSynthesisUtterance(clean);
      utterance.lang = 'en-US';
      window.speechSynthesis.speak(utterance);
    });
  };

  return (
    <div className={`h-full flex flex-col overflow-hidden transition-colors duration-500 ${isDarkMode ? 'bg-[#1A1A1A] text-white' : 'bg-gray-50 text-gray-900'}`}>
      <main className="flex-1 overflow-hidden relative flex flex-col">
        <AnimatePresence mode="wait">
          {screen === 'language_select' ? (
            <LanguageSelectScreen 
              key="language_select" 
              onSelect={handleInitialLanguageSelect} 
            />
          ) : screen === 'welcome' ? (
            <WelcomeScreen 
              key="welcome" 
              onStart={() => setScreen('menu')} 
              skipWelcome={skipWelcome} 
              setSkipWelcome={setSkipWelcome} 
              language={language}
            />
          ) : screen === 'alphabet_grid' ? (
            <AlphabetGridScreen 
              key="abc" 
              onBack={handleBack} 
              onPlayLetter={playSound} 
              language={language}
            />
          ) : screen === 'practice' ? (
            <PracticeScreen 
               key="practice"
               onBack={handleBack}
               activeLesson={activeLesson}
               setActiveLesson={setActiveLesson}
               lessonStep={lessonStep}
               setLessonStep={setLessonStep}
               completedLessons={completedLessons}
               setCompletedLessons={setCompletedLessons}
               language={language}
               localizedPhonemes={localizedPhonemes}
            />
          ) : screen === 'menu' ? (
            <motion.div key="menu" initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="flex-1 flex flex-col items-center justify-center p-8">
              <button 
                onClick={() => setIsSettingsOpen(true)}
                className="absolute top-6 right-6 p-3 bg-white/5 border border-white/10 rounded-2xl text-white/40 hover:text-white"
              >
                <Settings />
              </button>
              <div className="mb-12 text-center">
                <h1 className="text-3xl font-black italic uppercase mb-2">
                  {language === 'ua' ? "БАЗА ДЛЯ СТАРТУ В АНГЛІЙСЬКУ" : "БАЗА ДЛЯ СТАРТА В АНГЛИЙСКИЙ"}
                </h1>
                <p className="text-white/40 text-sm">
                  {language === 'ua' ? "(навичка читати, писати, говорити, розуміти)" : "(навык читать, писать, говорить, понимать)"}
                </p>
              </div>
              <div className="grid gap-3 w-full max-w-sm">
                <button onClick={() => setScreen('alphabet_grid')} className="p-6 bg-white/5 border border-white/10 rounded-[32px] text-left hover:bg-white/10 transition-all flex justify-between items-center group">
                  <div>
                    <h3 className="text-xl font-black text-green-500 tracking-tighter italic uppercase">
                      {language === 'ua' ? "26 букв алфавіту" : "26 букв алфавита"}
                    </h3>
                    <p className="text-xs text-white/40">
                      {language === 'ua' ? "Назви букв" : "Названия букв"}
                    </p>
                  </div>
                  <span className="text-4xl font-black text-white/5 group-hover:text-white/10 transition-colors italic">26</span>
                </button>
                <button onClick={() => { setScreen('phonemes'); setActiveTab('sounds'); }} className="p-6 bg-white/5 border border-white/10 rounded-[32px] text-left hover:bg-white/10 transition-all flex justify-between items-center group">
                  <div>
                    <h3 className="text-xl font-black text-green-500 tracking-tighter italic uppercase">
                      {language === 'ua' ? "44 звуки" : "44 звука"}
                    </h3>
                    <p className="text-xs text-white/40">
                      {language === 'ua' ? "Вивчення фонетики" : "Изучение фонетики"}
                    </p>
                  </div>
                  <span className="text-4xl font-black text-white/5 group-hover:text-white/10 transition-colors italic">44</span>
                </button>
                <button onClick={() => setScreen('practice')} className="p-6 bg-white/5 border border-white/10 rounded-[32px] text-left hover:bg-white/10 transition-all flex justify-between items-center group">
                  <div>
                    <h3 className="text-xl font-black text-green-500 tracking-tighter italic uppercase">
                      {language === 'ua' ? "Практика" : "Практика"}
                    </h3>
                    <p className="text-xs text-white/40">
                      {language === 'ua' ? "Уроки та вправи" : "Уроки и упражнения"}
                    </p>
                  </div>
                  <Gamepad2 className="w-10 h-10 text-white/5 group-hover:text-white/10 transition-colors" />
                </button>
              </div>
            </motion.div>
          ) : screen === 'phonemes' ? (
            <div key="phonemes" className="flex-1 flex flex-col overflow-hidden">
               <div className="p-4 flex items-center gap-4 border-b border-white/10">
                 <button onClick={handleBack} className="p-2 text-white/40"><ChevronLeft /></button>
                 <button onClick={() => playSound(currentSelectedPhoneme.phonetic || currentSelectedPhoneme.id)} className="w-12 h-12 rounded-xl bg-white/5 border border-white/10 flex items-center justify-center text-xl font-bold text-green-500 italic">[{currentSelectedPhoneme.symbol}]</button>
               </div>
               <div className="flex-1 overflow-y-auto p-6 flex flex-col items-center">
                  <div className="text-center mb-8">
                    <h1 className="text-5xl font-black italic uppercase text-white mb-2">{currentSelectedPhoneme.example}</h1>
                    <p className="text-xl text-white/40 font-bold tracking-widest">{currentSelectedPhoneme.transcription}</p>
                    <p className="text-md italic text-white/60 font-serif mt-2">{currentSelectedPhoneme.translation}</p>
                  </div>
                  <div className="w-40 h-40 rounded-[40px] overflow-hidden border-2 border-white/10 mb-8" onClick={() => playSound(currentSelectedPhoneme.example)}>
                    <img src={`https://picsum.photos/seed/${currentSelectedPhoneme.example}/400/400`} alt="" className="w-full h-full object-cover" />
                  </div>
                  <div className="p-4 bg-white/5 rounded-2xl border border-white/10 text-center">
                    <p className="text-xs text-green-500/80 leading-relaxed font-bold">{currentSelectedPhoneme.description}</p>
                  </div>
               </div>
               <div className="bg-white/5 border-t border-white/10 p-4">
                  <div className="flex gap-2 mb-4">
                    {Object.keys(categories).map(cat => (
                      <button 
                        key={cat} 
                        onClick={() => setActiveCategory(cat)}
                        className={`flex-1 py-2 text-[10px] font-black uppercase tracking-widest rounded-full transition-all ${activeCategory === cat ? 'bg-green-600 text-white' : 'bg-white/5 text-white/40'}`}
                      >
                        {cat === 'СОГЛАСНЫЕ' 
                          ? (language === 'ua' ? 'ПРИГОЛОСНІ' : 'СОГЛАСНЫЕ') 
                          : (language === 'ua' ? 'ГОЛОСНІ' : 'ГЛАСНЫЕ')}
                      </button>
                    ))}
                  </div>
                  <div className="grid grid-cols-6 gap-2 max-h-48 overflow-y-auto no-scrollbar">
                    {(categories as any)[activeCategory].map((p: Phoneme) => (
                      <button 
                        key={p.id} 
                        onClick={() => { setSelectedPhoneme(p); playSound(p.phonetic || p.id); }}
                        className={`aspect-square border-2 rounded-xl flex items-center justify-center text-lg font-bold transition-all ${selectedPhoneme.id === p.id ? 'bg-green-600 border-green-400 text-white' : 'bg-white/5 border-white/5 text-white/40 hover:bg-white/10'}`}
                      >
                        {p.symbol}
                      </button>
                    ))}
                  </div>
               </div>
            </div>
          ) : null}
        </AnimatePresence>
      </main>

      {/* --- BOTTOM NAV --- */}
      {screen !== 'welcome' && screen !== 'language_select' && (
        <nav className="h-20 bg-[#121212] border-t border-white/10 flex items-center justify-around px-6 shrink-0 z-10 transition-colors duration-500">
          <button onClick={() => { setScreen('menu'); setActiveTab('home'); }} className={`flex flex-col items-center gap-1 ${activeTab === 'home' ? 'text-white' : 'text-white/40'}`}>
            <Home className="w-6 h-6" />
            <span className="text-[10px] font-bold">
              {language === 'ua' ? "Головна" : "Главная"}
            </span>
          </button>
          <button onClick={() => { setScreen('practice'); setActiveTab('practice'); }} className={`flex flex-col items-center gap-1 ${activeTab === 'practice' ? 'text-green-500' : 'text-white/40'}`}>
            <Gamepad2 className="w-6 h-6" />
            <span className="text-[10px] font-bold">
              {language === 'ua' ? "Уроки" : "Уроки"}
            </span>
          </button>
          <button onClick={() => { setScreen('phonemes'); setActiveTab('sounds'); }} className={`flex flex-col items-center gap-1 ${activeTab === 'sounds' ? 'text-green-500' : 'text-white/40'}`}>
            <Mic className="w-6 h-6" />
            <span className="text-[10px] font-bold">
              {language === 'ua' ? "База звуків" : "База звуков"}
            </span>
          </button>
          <button onClick={() => { setScreen('alphabet_grid'); setActiveTab('letters'); }} className={`flex flex-col items-center gap-1 ${activeTab === 'letters' ? 'text-green-500' : 'text-white/40'}`}>
            <Type className="w-6 h-6" />
            <span className="text-[10px] font-bold">
              {language === 'ua' ? "База букв" : "База букв"}
            </span>
          </button>
        </nav>
      )}

      {isSettingsOpen && (
        <SettingsDialog 
           isDarkMode={isDarkMode} 
           onDarkModeChange={setIsDarkMode} 
           language={language}
           onLanguageChange={handleLanguageChange}
           onDismiss={() => setIsSettingsOpen(false)} 
           onResetProgress={() => { setCompletedLessons(0); setIsSettingsOpen(false); }}
           onResetSettings={() => { localStorage.clear(); window.location.reload(); }}
           onCopyKotlin={() => { setShowKotlinCode(true); setIsSettingsOpen(false); }}
        />
      )}

      <AnimatePresence>
        {showKotlinCode && (
          <div className="fixed inset-0 z-[100] bg-black/90 backdrop-blur-xl p-6 flex flex-col" onClick={() => setShowKotlinCode(false)}>
            <div className="flex justify-between items-center mb-6" onClick={e => e.stopPropagation()}>
              <div>
                 <h2 className="text-xl font-bold">
                   {language === 'ua' ? "MainActivity.kt" : "MainActivity.kt"}
                 </h2>
                 <p className="text-xs text-white/40">
                   {language === 'ua' ? "Повний код Android додатка" : "Полный код Android приложения"}
                 </p>
              </div>
              <button 
                onClick={() => {
                  navigator.clipboard.writeText(KOTLIN_CODE);
                  setCopied(true);
                  setTimeout(() => setCopied(false), 2000);
                }}
                className="flex items-center gap-2 px-4 py-2 bg-green-600 rounded-xl font-bold text-xs"
              >
                {copied ? <Check className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
                {copied ? (language === 'ua' ? 'КОПІЙОВАНО' : 'КОПИРОВАНО') : (language === 'ua' ? 'КОПІЮВАТИ' : 'КОПИРОВАТЬ')}
              </button>
            </div>
            <div className="flex-1 bg-white/5 border border-white/10 rounded-2xl overflow-hidden" onClick={e => e.stopPropagation()}>
              <pre className="p-6 text-[10px] font-mono text-green-200/80 overflow-auto h-full no-scrollbar">
                {KOTLIN_CODE}
              </pre>
            </div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}
