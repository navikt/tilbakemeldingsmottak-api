import { RESET_KLASSIFISERING, UPDATE_ANSWER, UPDATE_SCHEMA } from "./actions";

const updateRenderAnswers = (answers, value, index) => {
  const prevAnswers = index === 0 ? [] : answers.slice(0, index);
  return ((value.answer == null || value.answer == undefined) && !value.optional
    ? prevAnswers
    : [
        ...prevAnswers,
        ...(answers.length > index && answers[index].next !== value.next
          ? [value]
          : [value, ...answers.slice(index + 1, answers.length)])
      ]
  ).map(answer => ({
    ...answer,
    question: { ...answer.question }
  }));
};

const updateAnswersStatus = (answers, questions) => {
  return {
    progress: {
      index: answers.length >= 1 ? answers[answers.length - 1].next : 0,
      numberOfQuestions: questions.length - 1
    },
    answers: answers.reduce(
      (acc, answer) => ({
        ...acc,
        [answer.id]: answer.answer
      }),
      {}
    )
  };
};

const updateAnswers = (answers, questions, value, index) => {
  const renderAnswers = updateRenderAnswers(answers, value, index);
  return {
    answers: renderAnswers,
    status: updateAnswersStatus(renderAnswers, questions)
  };
};

const initialState = {
  defaultAnswers: {
    answers: {}
  },
  questions: [],
  answers: [],
  status: {
    progress: {},
    answers: {}
  }
};

export default (state = initialState, action) => {
  switch (action.type) {
    case UPDATE_SCHEMA:
      return {
        ...state,
        defaultAnswers: action.defaultAnswers,
        questions: action.questions
      };
    case UPDATE_ANSWER:
      return {
        ...state,
        ...updateAnswers(
          state.answers,
          state.questions,
          action.value,
          action.index
        )
      };
    case RESET_KLASSIFISERING:
      return { ...state, ...initialState };
    default:
      return state;
  }
};
