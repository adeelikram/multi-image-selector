declare module '@capacitor/core' {
  interface PluginRegistry {
    getImages: getImagesPlugin;
  }
}

export interface getImagesPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  getImagesFromGallery(options:{quality:Number}):Promise<{value:any[]}>
}
