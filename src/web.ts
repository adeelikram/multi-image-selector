import { WebPlugin } from '@capacitor/core';
import { getImagesPlugin } from './definitions';

export class getImagesWeb extends WebPlugin implements getImagesPlugin {
  constructor() {
    super({
      name: 'getImages',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
  async getImagesFromGallery(options: { quality: Number }): Promise<{ value: any[] }> {
    console.log('ECHO', options);
    return {value:[]};
  }
}

const getImages = new getImagesWeb();

export { getImages };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(getImages);
