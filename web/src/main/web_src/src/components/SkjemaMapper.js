const classifyRoutes = (question, route) => ({
    ...question,
    ...(question.answers
      ? {
          answers: question.answers.map((answer, answerIndex) => ({
            ...(typeof answer === 'string' ? { answer } : answer),
            ...(answer.questions
              ? {
                  questions: answer.questions.map(nestedQuestion =>
                    classifyRoutes(nestedQuestion, `${route}${answerIndex}`)
                  )
                }
              : {})
          }))
        }
      : {}),
    route: route || 'unknown'
  })
  
  const flattenNestedRoutes = question => {
    return [
      question,
      ...(question.answers
        ? question.answers.map(answer =>
            (answer.questions
              ? answer.questions.map(flattenNestedRoutes)
              : []
            ).flat()
          )
        : [])
    ]
      .flat()
      .filter(question => question)
  }
  
  const getChildRoute = (answerIndex, route, routeArray) => {
    return routeArray.indexOf(`${route}${answerIndex}`)
  }
  
  const getSiblingRoute = (questionIndex, route, routeArray) => {
    const position = routeArray.slice(questionIndex + 1).indexOf(route)
    return position !== -1 ? position + questionIndex : position
  }
  
  const getParentRoute = (questionIndex, route, routeArray) => {
    const getParentRouteSlice = (route, routeArray) => {
      if (route.length === 1) {
        const parentNode = routeArray.indexOf(`${parseInt(route) + 1}`)
        return parentNode !== -1 ? parentNode + questionIndex : parentNode
      }
      const parentRoute = route.substring(0, route.length - 1)
      const parent = routeArray.slice(questionIndex + 1).indexOf(parentRoute)
      return parent !== -1
        ? parent + questionIndex
        : getParentRouteSlice(parentRoute, routeArray)
    }
    return getParentRouteSlice(route, routeArray.slice(questionIndex + 1))
  }
  
  const getNext = ({ next, index, routeIds, route, routeArray, answerIndex }) => {
    if (next != null) {
      const routeId = routeIds.indexOf(next)
      if (routeId !== -1) {
        return routeId + 1
      }
    }
    if (answerIndex === 0 || answerIndex) {
      const childRoute = getChildRoute(answerIndex, route, routeArray)
      if (childRoute !== -1) {
        return childRoute + 1
      }
    }
    const siblingRoute = getSiblingRoute(index, route, routeArray)
    if (siblingRoute !== -1) {
      return siblingRoute + 2
    }
    const parentRoute = getParentRoute(index, route, routeArray)
    return parentRoute !== -1 ? parentRoute + 2: 'none'
  }
  
  const indexAnswers = (question, routeArray, routeIds, index) => ({
    answers: question.answers.map((answer, answerIndex) => ({
      answer: answer.answer,
      next: answer.next === "none" ? "none" :
        getNext({
          route: question.route,
          index,
          routeArray,
          routeIds,
          answerIndex
        })
    }))
  })
  
  const indexRoutes = (questions, routeArray, routeIds, startIndex = 0) => 
    questions.map((question, index) => 
       ({
      title: question.title,
      id: question.id,
      type: question.type,
      next:
        question.next === 'none'
          ? 'none'
          : getNext({
              ...question,
              index: index + startIndex,
              routeArray,
              routeIds
            }),
      ...(question.answers
        ? indexAnswers(question, routeArray, routeIds, index + startIndex)
        : {})
    })
  )
  
  export default schema => {
    const questions = schema.questions || []
    const routes = questions.map((question, index) =>
      classifyRoutes(question, `${index}`)
    )
    const flatRoutes = routes.map(flattenNestedRoutes).flat()
    const routeArray = flatRoutes.map(questions => questions.route)
    const routeIds = flatRoutes.map(question => question.id)
  
    return indexRoutes(flatRoutes, routeArray, routeIds)
  }
  