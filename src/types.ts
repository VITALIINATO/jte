export type SoundCategory = 'monophthong' | 'diphthong' | 'plosive' | 'affricate' | 'fricative' | 'nasal' | 'liquid-glide';

export interface Phoneme {
  id: string;
  symbol: string;
  letterLabel: string;
  example: string;
  transcription: string;
  translation: string;
  description: string;
  imageUrl: string;
  category: SoundCategory;
  audioUrl?: string;
  letterAudioUrl?: string;
  color: string;
  phonetic: string;
  lipSteps?: string[];
  letterCombinations?: string[];
}
