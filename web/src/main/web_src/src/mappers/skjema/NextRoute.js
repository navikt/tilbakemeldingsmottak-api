const findIndexOfNestedArray = (routeArray, array) => {
  return routeArray.findIndex(
    route =>
      route.length === array.length &&
      route.every((element, index) => element === array[index])
  );
};

const getChildRoute = ({ answerIndex, route, routeArray }) => {
  if (answerIndex < 0) {
    return -1;
  }
  return findIndexOfNestedArray(routeArray, [...route, answerIndex]);
};

const getSiblingRoute = ({ questionIndex, route, routeArray }) => {
  const position = findIndexOfNestedArray(
    routeArray.slice(questionIndex + 1),
    route
  );
  return position !== -1 ? position + questionIndex + 1 : position;
};

const getParentRoute = ({ questionIndex, route, routeArray }) => {
  const getParentRouteSlice = (route, routeArray) => {
    if (route.length === 1) {
      const parentNode = findIndexOfNestedArray(routeArray, [route[0] + 1]);
      return parentNode !== -1 ? parentNode : parentNode;
    }
    const parent = findIndexOfNestedArray(routeArray, route);
    return parent !== -1
      ? parent
      : getParentRouteSlice(route.slice(0, -1), routeArray);
  };
  const position = getParentRouteSlice(
    route,
    routeArray.slice(questionIndex + 1)
  );
  return position !== -1 ? position + questionIndex + 1 : -1;
};

export default ({
  answerIndex,
  questionIndex,
  route,
  routeArray,
  routeIds,
  next
}) => {
  if (next === "none") {
    return next;
  } else if (next != null && routeIds.indexOf(next) !== -1) {
    return routeIds.indexOf(next) + 1;
  } else {
    const index = [getChildRoute, getSiblingRoute, getParentRoute]
      .map(func =>
        func({
          answerIndex,
          questionIndex,
          route,
          routeArray
        })
      )
      .find(ind => ind !== -1);
    return index ? index + 1 : "none";
  }
};
