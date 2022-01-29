import numpy as np
import cv2
import matplotlib.pyplot as plt
import numpy as np
import random


def plot_visualization(image, pred_boxes, pred_masks, pred_class, pred_score, save_location='', output_name='output.png', show_image=False):
  '''
    Visualize the output of the model.
    Arguments:
      image (numpy array): The image to be displayed.
      pred_boxes (list): A list of bounding boxes containing elements of type [(X1, Y1), (X2, Y2)].
      pred_masks (list): A list of masks of shape (1, H, W).
      pred_class (list): A list of class labels.
      pred_score (list): A list of scores which give confidence in the prediction.
      save_location (str): The location to save the visualization.
      output_name (str): The name of the output image.
      show_image (bool): Whether to show the image.
  '''

  number_of_boxes = len(pred_boxes)
  if(number_of_boxes<=3):
    indices = range(number_of_boxes)
  else:
    temp = dict()
    for i in range(number_of_boxes):
      temp[pred_score[i]] = i
    indices = [temp[x] for x in temp]
    while(len(indices)>3):
      indices.pop()
  for i in indices:
    print(i)
    color = [random.randint(0, 255) for _ in range(3)]
    (x1, y1), (x2, y2) = pred_boxes[i]
    name = pred_class[i]
    confidence = pred_score[i]
    image = cv2.rectangle(image, (x1, y1), (x2, y2), color, 1)
    image = cv2.putText(image, '{}: {:.3f}'.format(name, confidence), (x1, y1), cv2.FONT_HERSHEY_SIMPLEX, 0.5, color, 1)
    mask = pred_masks[i][0, :, :]
    mask = np.stack((color[0]*mask, color[1]*mask, color[2]*mask), axis=-1).astype(np.uint8)
    image = cv2.addWeighted(mask, 0.8, image.astype(np.uint8), 1, 0)
  if show_image:
    plt.imshow(image)
    plt.show()
  cv2.imwrite(save_location+'/'+output_name, image)
  return