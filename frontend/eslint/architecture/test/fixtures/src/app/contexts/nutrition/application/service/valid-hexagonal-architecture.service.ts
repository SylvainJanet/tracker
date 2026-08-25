import type { FoodEntry } from '../../domain/food-entry';
import type { ValidReservedVocabularyUseCase } from '../port/in/valid-reserved-vocabulary.use-case';

export class ValidHexagonalArchitectureService implements ValidReservedVocabularyUseCase {
  execute(): FoodEntry {
    throw new Error('Fixture only');
  }
}
