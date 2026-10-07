import type { Provider } from '@angular/core';

import {
  SHARED_GRAPH_PRESENTER_FACTORY,
  SharedGraphPresenter,
  type SharedGraphPresenterFactory,
} from '../adapter/in/web/graph/presenter/shared.graph.presenter';

export function provideSharedGraphPresenter(): Provider {
  return {
    provide: SHARED_GRAPH_PRESENTER_FACTORY,
    useValue: (() => new SharedGraphPresenter()) satisfies SharedGraphPresenterFactory,
  };
}
